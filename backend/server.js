const express = require('express');
const cors = require('cors');
const { v4: uuidv4 } = require('uuid');
const multer = require('multer');
const data = require('./data');

const app = express();
const PORT = 3000;
const upload = multer({ storage: multer.memoryStorage() });

app.use(cors());
app.use(express.json());

// ─── Health check ───
app.get('/', (req, res) => {
  res.json({ status: 'ok', name: 'Golazo Medical API', version: '1.0.0' });
});

// ═══════════════════════════════════════════
//  AUTH ROUTES
// ═══════════════════════════════════════════

// POST /api/auth/login
app.post('/api/auth/login', (req, res) => {
  const { email, password, role, isRegistration, nonUefa } = req.body;

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
    data.users.push(newUser);
    const { password: _, ...safeUser } = newUser;
    return res.json({ user: safeUser, requires2FA: true, isNewUser: true });
  }

  const user = data.users.find(u => u.email === email && u.role === (role || u.role));
  if (!user || user.password !== password) {
    return res.status(401).json({ error: 'Invalid email or password' });
  }

  user.last_login_at = new Date().toISOString();
  const { password: _, ...safeUser } = user;
  return res.json({ user: safeUser, requires2FA: true, isNewUser: false });
});

// POST /api/auth/request-pin
app.post('/api/auth/request-pin', (req, res) => {
  const { userId } = req.body;
  const user = data.users.find(u => u.id === userId);
  if (!user) return res.status(404).json({ success: false });

  const pin = '1234'; // Fixed demo PIN
  data.pins[userId] = pin;
  console.log(`[PIN] User ${userId}: ${pin}`);

  res.json({
    success: true,
    pin: pin, // Returned for demo convenience
    phoneNumber: user.phone_number || '+00 000 000 000'
  });
});

// POST /api/auth/verify-pin
app.post('/api/auth/verify-pin', (req, res) => {
  const { userId, pin } = req.body;
  const expectedPin = data.pins[userId] || '1234';

  if (pin !== expectedPin) {
    return res.json({ verified: false, user: null });
  }

  const user = data.users.find(u => u.id === userId);
  if (!user) return res.status(404).json({ verified: false, user: null });

  const { password: _, ...safeUser } = user;
  res.json({ verified: true, user: safeUser });
});

// POST /api/auth/complete-onboarding
app.post('/api/auth/complete-onboarding', (req, res) => {
  const { userId, phoneNumber } = req.body;
  const user = data.users.find(u => u.id === userId);
  if (!user) return res.status(404).json({ user: null });

  user.phone_number = phoneNumber;
  user.onboarding_complete = true;

  const { password: _, ...safeUser } = user;
  res.json({ user: safeUser });
});

// POST /api/auth/accept-terms
app.post('/api/auth/accept-terms', (req, res) => {
  const { userId, signature } = req.body;
  const user = data.users.find(u => u.id === userId);
  if (!user) return res.status(404).json({ user: null });

  user.tc_accepted_at = new Date().toISOString();
  user.tc_signature = signature;

  const { password: _, ...safeUser } = user;
  res.json({ user: safeUser });
});

// GET /api/auth/inactive-players
app.get('/api/auth/inactive-players', (req, res) => {
  const inactivePlayers = data.users
    .filter(u => u.role === 'player' && !u.onboarding_complete)
    .map(u => {
      const profile = data.profiles.find(p => p.user_id === u.id) || null;
      const { password: _, ...safeUser } = u;
      return { user: safeUser, profile };
    });
  res.json({ players: inactivePlayers });
});

// ═══════════════════════════════════════════
//  USER ROUTES
// ═══════════════════════════════════════════

// GET /api/users/:userId
app.get('/api/users/:userId', (req, res) => {
  const user = data.users.find(u => u.id === req.params.userId);
  if (!user) return res.status(404).json({ user: null });

  const { password: _, ...safeUser } = user;
  res.json({ user: safeUser });
});

// ═══════════════════════════════════════════
//  PROFILE ROUTES
// ═══════════════════════════════════════════

// GET /api/profile/:userId
app.get('/api/profile/:userId', (req, res) => {
  const profile = data.profiles.find(p => p.user_id === req.params.userId);
  res.json({ profile: profile || null });
});

// POST /api/profile
app.post('/api/profile', (req, res) => {
  const { userId, firstName, lastName, nationality, club, dob, position, imageUrl, location } = req.body;

  const existing = data.profiles.find(p => p.user_id === userId);
  if (existing) {
    Object.assign(existing, {
      first_name: firstName, last_name: lastName, nationality, club, dob, position,
      image_url: imageUrl || null, location: location || null,
      updated_at: new Date().toISOString()
    });
    return res.json({ profile: existing });
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
  data.profiles.push(profile);
  res.json({ profile });
});

// ═══════════════════════════════════════════
//  CONSENT ROUTES
// ═══════════════════════════════════════════

// GET /api/consent/:userId
app.get('/api/consent/:userId', (req, res) => {
  const grants = data.consents.filter(c => c.user_id === req.params.userId);
  res.json({ grants });
});

// POST /api/consent
app.post('/api/consent', (req, res) => {
  const { userId, granteeType, granteeName, granteeOrg, scopes, isDefault, recipientEmail } = req.body;

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
  data.consents.push(grant);

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
    data.invitations.push(invitation);
  }

  res.json({ grant, invitation });
});

// DELETE /api/consent/:id
app.delete('/api/consent/:id', (req, res) => {
  const idx = data.consents.findIndex(c => c.id === req.params.id);
  if (idx === -1) return res.status(404).json({ success: false });
  data.consents.splice(idx, 1);
  res.json({ success: true });
});

// GET /api/invitation/:token
app.get('/api/invitation/:token', (req, res) => {
  const invitation = data.invitations.find(i => i.token === req.params.token);
  res.json({ invitation: invitation || null });
});

// ═══════════════════════════════════════════
//  PCME ROUTES
// ═══════════════════════════════════════════

// GET /api/pcme/:userId
app.get('/api/pcme/:userId', (req, res) => {
  const entries = data.pcmeEntries.filter(e => e.user_id === req.params.userId);
  res.json({ entries });
});

// GET /api/pcme/entry/:id
app.get('/api/pcme/entry/:id', (req, res) => {
  const entry = data.pcmeEntries.find(e => e.id === req.params.id);
  res.json({ entry: entry || null });
});

// POST /api/pcme
app.post('/api/pcme', (req, res) => {
  const entry = {
    ...req.body,
    id: req.body.id || uuidv4(),
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  };
  data.pcmeEntries.push(entry);
  res.json({ entry });
});

// PUT /api/pcme/:id
app.put('/api/pcme/:id', (req, res) => {
  const idx = data.pcmeEntries.findIndex(e => e.id === req.params.id);
  if (idx === -1) return res.status(404).json({ entry: null });

  data.pcmeEntries[idx] = {
    ...data.pcmeEntries[idx],
    ...req.body,
    id: req.params.id,
    updated_at: new Date().toISOString()
  };
  res.json({ entry: data.pcmeEntries[idx] });
});

// ═══════════════════════════════════════════
//  INJURY ROUTES
// ═══════════════════════════════════════════

// GET /api/injuries/:userId
app.get('/api/injuries/:userId', (req, res) => {
  const userInjuries = data.injuries.filter(i => i.user_id === req.params.userId);
  res.json({ injuries: userInjuries });
});

// GET /api/injury/:id
app.get('/api/injury/:id', (req, res) => {
  const injury = data.injuries.find(i => i.id === req.params.id);
  res.json({ injury: injury || null });
});

// POST /api/injuries
app.post('/api/injuries', (req, res) => {
  const injury = {
    ...req.body,
    id: req.body.id || uuidv4(),
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  };
  data.injuries.push(injury);
  res.json({ injury });
});

// PATCH /api/injury/:id
app.patch('/api/injury/:id', (req, res) => {
  const idx = data.injuries.findIndex(i => i.id === req.params.id);
  if (idx === -1) return res.status(404).json({ injury: null });

  data.injuries[idx] = {
    ...data.injuries[idx],
    ...req.body,
    id: req.params.id,
    updated_at: new Date().toISOString()
  };
  res.json({ injury: data.injuries[idx] });
});

// DELETE /api/injury/:id
app.delete('/api/injury/:id', (req, res) => {
  const idx = data.injuries.findIndex(i => i.id === req.params.id);
  if (idx === -1) return res.status(404).json({ success: false });
  data.injuries.splice(idx, 1);
  res.json({ success: true });
});

// ═══════════════════════════════════════════
//  INJURY NOTES ROUTES
// ═══════════════════════════════════════════

// GET /api/injury/:injuryId/notes
app.get('/api/injury/:injuryId/notes', (req, res) => {
  const notes = data.injuryNotes.filter(n => n.injury_case_id === req.params.injuryId);
  res.json({ notes });
});

// POST /api/injury/:injuryId/notes
app.post('/api/injury/:injuryId/notes', (req, res) => {
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
  data.injuryNotes.push(note);
  res.json({ note });
});

// ═══════════════════════════════════════════
//  TRAINING ROUTES
// ═══════════════════════════════════════════

// GET /api/training
app.get('/api/training', (req, res) => {
  res.json({ sessions: data.trainingSessions });
});

// POST /api/training
app.post('/api/training', (req, res) => {
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
  data.trainingSessions.push(session);
  res.json({ session });
});

// DELETE /api/training/:id
app.delete('/api/training/:id', (req, res) => {
  const idx = data.trainingSessions.findIndex(s => s.id === req.params.id);
  if (idx === -1) return res.status(404).json({ success: false });
  data.trainingSessions.splice(idx, 1);
  res.json({ success: true });
});

// ═══════════════════════════════════════════
//  DOCTOR ROUTES
// ═══════════════════════════════════════════

// GET /api/doctor/players
app.get('/api/doctor/players', (req, res) => {
  const players = data.users
    .filter(u => u.role === 'player')
    .map(u => {
      const profile = data.profiles.find(p => p.user_id === u.id) || null;
      const { password: _, ...safeUser } = u;
      return { user: safeUser, profile };
    });
  res.json({ players });
});

// GET /api/doctor/players/:userId
app.get('/api/doctor/players/:userId', (req, res) => {
  const user = data.users.find(u => u.id === req.params.userId);
  if (!user) return res.status(404).json({ user: null, profile: null, injuries: [], pcmeEntries: [], trainingSessions: [] });

  const { password: _, ...safeUser } = user;
  const profile = data.profiles.find(p => p.user_id === user.id) || null;
  const userInjuries = data.injuries.filter(i => i.user_id === user.id);
  const userPcme = data.pcmeEntries.filter(e => e.user_id === user.id);

  res.json({
    user: safeUser,
    profile,
    injuries: userInjuries,
    pcmeEntries: userPcme,
    trainingSessions: data.trainingSessions
  });
});

// POST /api/doctor/players/:userId/invite
app.post('/api/doctor/players/:userId/invite', (req, res) => {
  const { email, phoneNumber } = req.body;
  const user = data.users.find(u => u.id === req.params.userId);

  if (!user) {
    // Create a new player
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
    data.users.push(newUser);
  }

  const profile = data.profiles.find(p => p.user_id === req.params.userId) || null;
  res.json({ success: true, profile });
});

// GET /api/doctor/injuries
app.get('/api/doctor/injuries', (req, res) => {
  res.json({ injuries: data.injuries });
});

// GET /api/doctor/pcme
app.get('/api/doctor/pcme', (req, res) => {
  res.json({ entries: data.pcmeEntries });
});

// GET /api/doctor/training
app.get('/api/doctor/training', (req, res) => {
  res.json({ sessions: data.trainingSessions });
});

// ═══════════════════════════════════════════
//  INTELLIGENCE / CHAT
// ═══════════════════════════════════════════

// POST /api/graph-ai/chat
app.post('/api/graph-ai/chat', (req, res) => {
  const { message, graphContext, conversationHistory } = req.body;

  // Simple mock AI responses
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
app.post('/api/transcribe', upload.single('file'), (req, res) => {
  res.json({
    transcript: 'Player reported mild discomfort in the left knee area during training. No swelling observed. Recommended rest and ice application.'
  });
});

// ─── Start server ───
app.listen(PORT, () => {
  console.log(`\n🏥 Golazo Medical API running on http://localhost:${PORT}`);
  console.log(`\n📋 Demo accounts:`);
  console.log(`   Player: player@uefa.com / password123`);
  console.log(`   Player: marco@uefa.com  / password123`);
  console.log(`   Doctor: doctor@uefa.com / password123`);
  console.log(`   PIN for all: 1234\n`);
});
