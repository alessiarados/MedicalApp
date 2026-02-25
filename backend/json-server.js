const jsonServer = require('json-server');
const path = require('path');
const { v4: uuidv4 } = require('uuid');
const multer = require('multer');

const server = jsonServer.create();
const router = jsonServer.router(path.join(__dirname, 'db.json'));
const middlewares = jsonServer.defaults({
  static: path.join(__dirname, 'public')
});
const upload = multer({ storage: multer.memoryStorage() });

const PORT = 3000;

server.use(middlewares);
server.use(jsonServer.bodyParser);

// ─── Health check ───
server.get('/', (req, res) => {
  res.json({ status: 'ok', name: 'Golazo Medical API (json-server)', version: '2.0.0' });
});

// ═══════════════════════════════════════════
//  AUTH ROUTES
// ═══════════════════════════════════════════

// POST /api/auth/login
server.post('/api/auth/login', (req, res) => {
  const { email, password, role, isRegistration, nonUefa } = req.body;
  const db = router.db;

  if (isRegistration) {
    const newUser = {
      id: uuidv4(),
      email,
      password,
      role: role || 'player',
      phone_number: null,
      onboarding_complete: false,
      non_uefa: nonUefa || false,
      tc_accepted_at: null,
      tc_signature: null,
      last_login_at: new Date().toISOString(),
      created_at: new Date().toISOString()
    };
    db.get('users').push(newUser).write();
    const { password: _, ...safeUser } = newUser;
    return res.json({ user: safeUser, requires2FA: true, isNewUser: true });
  }

  const user = db.get('users').find(u => u.email === email && u.role === (role || u.role)).value();
  if (!user || user.password !== password) {
    return res.status(401).json({ error: 'Invalid email or password' });
  }

  db.get('users').find({ id: user.id }).assign({ last_login_at: new Date().toISOString() }).write();
  const { password: _, ...safeUser } = user;
  return res.json({ user: safeUser, requires2FA: true, isNewUser: false });
});

// POST /api/auth/request-pin
server.post('/api/auth/request-pin', (req, res) => {
  const { userId } = req.body;
  const db = router.db;
  const user = db.get('users').find({ id: userId }).value();
  if (!user) return res.status(404).json({ success: false });

  const pin = '1234';
  db.get('pins').set(userId, pin).write();
  console.log(`[PIN] User ${userId}: ${pin}`);

  res.json({
    success: true,
    pin: pin,
    phoneNumber: user.phone_number || '+00 000 000 000'
  });
});

// POST /api/auth/verify-pin
server.post('/api/auth/verify-pin', (req, res) => {
  const { userId, pin } = req.body;
  const db = router.db;
  const storedPin = db.get('pins').get(userId).value() || '1234';

  if (pin !== storedPin) {
    return res.json({ verified: false, user: null });
  }

  const user = db.get('users').find({ id: userId }).value();
  if (!user) return res.status(404).json({ verified: false, user: null });

  const { password: _, ...safeUser } = user;
  res.json({ verified: true, user: safeUser });
});

// POST /api/auth/complete-onboarding
server.post('/api/auth/complete-onboarding', (req, res) => {
  const { userId, phoneNumber } = req.body;
  const db = router.db;
  const user = db.get('users').find({ id: userId });
  if (!user.value()) return res.status(404).json({ user: null });

  user.assign({ phone_number: phoneNumber, onboarding_complete: true }).write();
  const { password: _, ...safeUser } = user.value();
  res.json({ user: safeUser });
});

// POST /api/auth/accept-terms
server.post('/api/auth/accept-terms', (req, res) => {
  const { userId, signature } = req.body;
  const db = router.db;
  const user = db.get('users').find({ id: userId });
  if (!user.value()) return res.status(404).json({ user: null });

  user.assign({ tc_accepted_at: new Date().toISOString(), tc_signature: signature }).write();
  const { password: _, ...safeUser } = user.value();
  res.json({ user: safeUser });
});

// GET /api/auth/inactive-players
server.get('/api/auth/inactive-players', (req, res) => {
  const db = router.db;
  const inactivePlayers = db.get('users')
    .filter(u => u.role === 'player' && !u.onboarding_complete)
    .value()
    .map(u => {
      const profile = db.get('profiles').find({ user_id: u.id }).value() || null;
      const { password: _, ...safeUser } = u;
      return { user: safeUser, profile };
    });
  res.json({ players: inactivePlayers });
});

// ═══════════════════════════════════════════
//  USER ROUTES
// ═══════════════════════════════════════════

// GET /api/users/:userId
server.get('/api/users/:userId', (req, res) => {
  const db = router.db;
  const user = db.get('users').find({ id: req.params.userId }).value();
  if (!user) return res.status(404).json({ user: null });

  const { password: _, ...safeUser } = user;
  res.json({ user: safeUser });
});

// ═══════════════════════════════════════════
//  PROFILE ROUTES
// ═══════════════════════════════════════════

// GET /api/profile/:userId
server.get('/api/profile/:userId', (req, res) => {
  const db = router.db;
  const profile = db.get('profiles').find({ user_id: req.params.userId }).value();

  res.json({ profile: profile || null });
});

// POST /api/profile
server.post('/api/profile', (req, res) => {
  const { userId, firstName, lastName, nationality, club, dob, position, imageUrl, location } = req.body;
  const db = router.db;

  const existing = db.get('profiles').find({ user_id: userId });
  if (existing.value()) {
    existing.assign({
      first_name: firstName, last_name: lastName, nationality, club, dob, position,
      image_url: imageUrl || null, location: location || null,
      updated_at: new Date().toISOString()
    }).write();
    return res.json({ profile: existing.value() });
  }

  const profile = {
    id: uuidv4(),
    user_id: userId,
    first_name: firstName,
    last_name: lastName,
    nationality, club, dob, position,
    image_url: imageUrl || null,
    location: location || null,
    status: 'active',
    pcme_status: 'missing',
    pcme_expected_date: null,
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  };
  db.get('profiles').push(profile).write();
  res.json({ profile });
});

// ═══════════════════════════════════════════
//  CONSENT ROUTES
// ═══════════════════════════════════════════

// GET /api/consent/:userId
server.get('/api/consent/:userId', (req, res) => {
  const db = router.db;
  const grants = db.get('consents').filter({ user_id: req.params.userId }).value();
  res.json({ grants });
});

// POST /api/consent
server.post('/api/consent', (req, res) => {
  const { userId, granteeType, granteeName, granteeOrg, scopes, isDefault, recipientEmail } = req.body;
  const db = router.db;

  const grant = {
    id: uuidv4(),
    user_id: userId,
    grantee_type: granteeType,
    grantee_name: granteeName,
    grantee_org: granteeOrg || null,
    scopes: scopes || [],
    is_default: isDefault || false,
    created_at: new Date().toISOString()
  };
  db.get('consents').push(grant).write();

  let invitation = null;
  if (recipientEmail) {
    invitation = {
      id: uuidv4(),
      token: uuidv4(),
      consent_grant_id: grant.id,
      recipient_email: recipientEmail,
      recipient_name: granteeName,
      grantee_org: granteeOrg || null,
      expires_at: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
      created_at: new Date().toISOString()
    };
    db.get('invitations').push(invitation).write();
  }

  res.json({ grant, invitation });
});

// DELETE /api/consent/:id
server.delete('/api/consent/:id', (req, res) => {
  const db = router.db;
  const consent = db.get('consents').find({ id: req.params.id }).value();
  if (!consent) return res.status(404).json({ success: false });
  db.get('consents').remove({ id: req.params.id }).write();
  res.json({ success: true });
});

// GET /api/invitation/:token
server.get('/api/invitation/:token', (req, res) => {
  const db = router.db;
  const invitation = db.get('invitations').find({ token: req.params.token }).value();
  res.json({ invitation: invitation || null });
});

// ═══════════════════════════════════════════
//  PCME ROUTES
// ═══════════════════════════════════════════

// GET /api/pcme/:userId
server.get('/api/pcme/:userId', (req, res) => {
  const db = router.db;
  const entries = db.get('pcmeEntries').filter({ user_id: req.params.userId }).value();
  res.json({ entries });
});

// GET /api/pcme/entry/:id
server.get('/api/pcme/entry/:id', (req, res) => {
  const db = router.db;
  const entry = db.get('pcmeEntries').find({ id: req.params.id }).value();
  res.json({ entry: entry || null });
});

// POST /api/pcme
server.post('/api/pcme', (req, res) => {
  const db = router.db;
  const entry = {
    ...req.body,
    id: req.body.id || uuidv4(),
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  };
  db.get('pcmeEntries').push(entry).write();
  res.json({ entry });
});

// PUT /api/pcme/:id
server.put('/api/pcme/:id', (req, res) => {
  const db = router.db;
  const existing = db.get('pcmeEntries').find({ id: req.params.id });
  if (!existing.value()) return res.status(404).json({ entry: null });

  existing.assign({ ...req.body, id: req.params.id, updated_at: new Date().toISOString() }).write();
  res.json({ entry: existing.value() });
});

// ═══════════════════════════════════════════
//  INJURY ROUTES
// ═══════════════════════════════════════════

// GET /api/injuries/:userId
server.get('/api/injuries/:userId', (req, res) => {
  const db = router.db;
  const userInjuries = db.get('injuries').filter({ user_id: req.params.userId }).value();
  res.json({ injuries: userInjuries });
});

// GET /api/injury/:id
server.get('/api/injury/:id', (req, res) => {
  const db = router.db;
  const injury = db.get('injuries').find({ id: req.params.id }).value();
  res.json({ injury: injury || null });
});

// POST /api/injuries
server.post('/api/injuries', (req, res) => {
  const db = router.db;
  const injury = {
    ...req.body,
    id: req.body.id || uuidv4(),
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  };
  db.get('injuries').push(injury).write();
  res.json({ injury });
});

// PATCH /api/injury/:id
server.patch('/api/injury/:id', (req, res) => {
  const db = router.db;
  const existing = db.get('injuries').find({ id: req.params.id });
  if (!existing.value()) return res.status(404).json({ injury: null });

  existing.assign({ ...req.body, id: req.params.id, updated_at: new Date().toISOString() }).write();
  res.json({ injury: existing.value() });
});

// DELETE /api/injury/:id
server.delete('/api/injury/:id', (req, res) => {
  const db = router.db;
  const injury = db.get('injuries').find({ id: req.params.id }).value();
  if (!injury) return res.status(404).json({ success: false });
  db.get('injuries').remove({ id: req.params.id }).write();
  res.json({ success: true });
});

// ═══════════════════════════════════════════
//  INJURY NOTES ROUTES
// ═══════════════════════════════════════════

// GET /api/injury/:injuryId/notes
server.get('/api/injury/:injuryId/notes', (req, res) => {
  const db = router.db;
  const notes = db.get('injuryNotes').filter({ injury_case_id: req.params.injuryId }).value();
  res.json({ notes });
});

// POST /api/injury/:injuryId/notes
server.post('/api/injury/:injuryId/notes', (req, res) => {
  const db = router.db;
  const note = {
    id: uuidv4(),
    injury_case_id: req.params.injuryId,
    author_id: req.body.authorId,
    intensity: req.body.intensity || 1,
    soap_notes: req.body.soapNotes || '',
    rtp_status: req.body.rtpStatus || null,
    estimated_return_date: req.body.estimatedReturnDate || null,
    attachments: req.body.attachments || [],
    created_at: new Date().toISOString()
  };
  db.get('injuryNotes').push(note).write();
  res.json({ note });
});

// ═══════════════════════════════════════════
//  TRAINING ROUTES
// ═══════════════════════════════════════════

// GET /api/training
server.get('/api/training', (req, res) => {
  const db = router.db;
  res.json({ sessions: db.get('trainingSessions').value() });
});

// POST /api/training
server.post('/api/training', (req, res) => {
  const db = router.db;
  const session = {
    id: uuidv4(),
    date: req.body.date,
    type: req.body.type,
    title: req.body.title,
    duration: req.body.duration,
    attendees: req.body.attendees,
    notes: req.body.notes || null,
    time_of_day: req.body.timeOfDay || 'morning',
    pitch: req.body.pitch || null,
    distance: req.body.distance || null,
    play_ids: req.body.playIds || null,
    created_at: new Date().toISOString()
  };
  db.get('trainingSessions').push(session).write();
  res.json({ session });
});

// DELETE /api/training/:id
server.delete('/api/training/:id', (req, res) => {
  const db = router.db;
  const session = db.get('trainingSessions').find({ id: req.params.id }).value();
  if (!session) return res.status(404).json({ success: false });
  db.get('trainingSessions').remove({ id: req.params.id }).write();
  res.json({ success: true });
});

// ═══════════════════════════════════════════
//  DOCTOR ROUTES
// ═══════════════════════════════════════════

// GET /api/doctor/players
server.get('/api/doctor/players', (req, res) => {
  const db = router.db;
  const players = db.get('users')
    .filter({ role: 'player' })
    .value()
    .map(u => {
      const profile = db.get('profiles').find({ user_id: u.id }).value() || null;
      const { password: _, ...safeUser } = u;
      return { user: safeUser, profile };
    });
  res.json({ players });
});

// GET /api/doctor/players/:userId
server.get('/api/doctor/players/:userId', (req, res) => {
  const db = router.db;
  const user = db.get('users').find({ id: req.params.userId }).value();
  if (!user) return res.status(404).json({ user: null, profile: null, injuries: [], pcmeEntries: [], trainingSessions: [] });

  const { password: _, ...safeUser } = user;
  const profile = db.get('profiles').find({ user_id: user.id }).value() || null;
  const userInjuries = db.get('injuries').filter({ user_id: user.id }).value();
  const userPcme = db.get('pcmeEntries').filter({ user_id: user.id }).value();

  res.json({
    user: safeUser,
    profile,
    injuries: userInjuries,
    pcmeEntries: userPcme,
    trainingSessions: db.get('trainingSessions').value()
  });
});

// POST /api/doctor/players/:userId/invite
server.post('/api/doctor/players/:userId/invite', (req, res) => {
  const { email, phoneNumber } = req.body;
  const db = router.db;
  const user = db.get('users').find({ id: req.params.userId }).value();

  if (!user) {
    const newUser = {
      id: req.params.userId,
      email,
      password: 'password123',
      role: 'player',
      phone_number: phoneNumber,
      onboarding_complete: false,
      non_uefa: false,
      tc_accepted_at: null,
      tc_signature: null,
      last_login_at: null,
      created_at: new Date().toISOString()
    };
    db.get('users').push(newUser).write();
  }

  const profile = db.get('profiles').find({ user_id: req.params.userId }).value() || null;
  res.json({ success: true, profile });
});

// GET /api/doctor/injuries
server.get('/api/doctor/injuries', (req, res) => {
  const db = router.db;
  res.json({ injuries: db.get('injuries').value() });
});

// GET /api/doctor/pcme
server.get('/api/doctor/pcme', (req, res) => {
  const db = router.db;
  res.json({ entries: db.get('pcmeEntries').value() });
});

// GET /api/doctor/training
server.get('/api/doctor/training', (req, res) => {
  const db = router.db;
  res.json({ sessions: db.get('trainingSessions').value() });
});

// ═══════════════════════════════════════════
//  INTELLIGENCE / CHAT
// ═══════════════════════════════════════════

// POST /api/graph-ai/chat
server.post('/api/graph-ai/chat', (req, res) => {
  const { message, graphContext, conversationHistory } = req.body;

  const responses = {
    default: "I'm the Golazo Medical AI assistant. I can help you analyze player health data, injury patterns, and medical records. What would you like to know?",
    injury: "Based on the current injury data, I can see patterns in muscle injuries during high-intensity training periods. Consider adjusting training load for players returning from injury.",
    pcme: "The PCME records show all players are up to date with their medical examinations. The next batch of examinations is due in approximately 3 months.",
    training: "Training load analysis shows a good balance between tactical, physical, and recovery sessions. The team's average distance covered is within optimal ranges.",
    player: "Player fitness levels are generally good. Two players are currently managing injuries with active return-to-play protocols."
  };

  let response = responses.default;
  const lowerMsg = message.toLowerCase();
  if (lowerMsg.includes('injury') || lowerMsg.includes('hurt')) response = responses.injury;
  else if (lowerMsg.includes('pcme') || lowerMsg.includes('medical') || lowerMsg.includes('exam')) response = responses.pcme;
  else if (lowerMsg.includes('training') || lowerMsg.includes('session') || lowerMsg.includes('load')) response = responses.training;
  else if (lowerMsg.includes('player') || lowerMsg.includes('fitness') || lowerMsg.includes('squad')) response = responses.player;

  const newHistory = [
    ...(conversationHistory || []),
    { role: 'user', content: message },
    { role: 'assistant', content: response }
  ];

  res.json({
    response,
    graphActions: [],
    conversationHistory: newHistory
  });
});

// ═══════════════════════════════════════════
//  TRANSCRIPTION (mock)
// ═══════════════════════════════════════════

// POST /api/transcribe
server.post('/api/transcribe', upload.single('file'), (req, res) => {
  res.json({
    transcript: 'Player reported mild discomfort in the left knee area during training. No swelling observed. Recommended rest and ice application.'
  });
});

// Use json-server router for any remaining routes (fallback)
server.use(router);

// ─── Start server ───
server.listen(PORT, () => {
  console.log(`\n🏥 Golazo Medical API (json-server) running on http://localhost:${PORT}`);
  console.log(`\n📋 Demo accounts:`);
  console.log(`   Player: player@uefa.com / password123`);
  console.log(`   Player: marco@uefa.com  / password123`);
  console.log(`   Doctor: doctor@uefa.com / password123`);
  console.log(`   PIN for all: 1234`);
  console.log(`\n📸 Profile images served from /public/images/`);
  console.log(`   Alex Martinez: http://localhost:${PORT}/images/alex-martinez.jpg`);
  console.log(`   Marco Rossi:   http://localhost:${PORT}/images/marco-rossi.jpg\n`);
});
