import type { Express, Request, Response } from "express";
import { storage } from "./storage.js";
import { z } from "zod";
import multer from "multer";
import crypto from "crypto";
import {
  insertUserSchema,
  insertPlayerProfileSchema,
  insertConsentGrantSchema,
  insertPcmeEntrySchema,
  insertInjuryCaseSchema,
  insertTrainingSessionSchema,
  users,
  playerProfiles,
  consentGrants,
  invitationTokens,
} from "../shared/schema.js";
import { db } from "../db/index.js";
import { eq, inArray } from "drizzle-orm";

const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: 25 * 1024 * 1024 },
});

// Simple in-memory 2FA PIN storage
const pinStore = new Map<string, { pin: string; expiresAt: number }>();

function generatePin(): string {
  return Math.floor(1000 + Math.random() * 9000).toString();
}

export function registerRoutes(app: Express): void {
  // ─── Health check ───
  app.get("/", (_req: Request, res: Response) => {
    res.json({ status: "ok", name: "Golazo Medical API", version: "2.0.0", database: "PostgreSQL" });
  });

  // ─── Transcription (stub — no OpenAI key needed for mobile) ───
  app.post("/api/transcribe", upload.single("audio"), async (req: Request, res: Response) => {
    try {
      if (!req.file) {
        return res.status(400).json({ error: "No audio file provided" });
      }
      // Return a placeholder transcript since we don't have OpenAI configured
      res.json({ transcript: "Voice transcription recorded." });
    } catch (error: any) {
      res.status(500).json({ error: error.message || "Transcription failed" });
    }
  });

  // ─── Get inactive players for activation ───
  app.get("/api/auth/inactive-players", async (_req: Request, res: Response) => {
    try {
      const players = await storage.listInactivePlayers();
      res.json({ players });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch inactive players" });
    }
  });

  // ─── Auth: Login ───
  app.post("/api/auth/login", async (req: Request, res: Response) => {
    try {
      const { email, password, role, isRegistration, nonUefa } = z
        .object({
          email: z.string().email(),
          password: z.string().min(4),
          role: z.enum(["player", "doctor"]),
          isRegistration: z.boolean().optional(),
          nonUefa: z.boolean().optional(),
        })
        .parse(req.body);

      let user = await storage.getUserByEmail(email);

      if (!user) {
        user = await storage.createUser({
          email,
          password,
          role,
          phoneNumber: null,
          onboardingComplete: false,
          nonUefa: nonUefa || false,
          lastLoginAt: null,
        });
        res.json({ user, requires2FA: false, isNewUser: true });
      } else if (isRegistration) {
        return res.status(409).json({ error: "Email already registered. Please sign in instead." });
      } else {
        if (user.onboardingComplete) {
          const pin = generatePin();
          const expiresAt = Date.now() + 5 * 60 * 1000;
          pinStore.set(user.id, { pin, expiresAt });
          console.log(`[2FA] PIN for ${user.email}: ${pin}`);
          res.json({ user, requires2FA: true, pin, isNewUser: false });
        } else {
          res.json({ user, requires2FA: false, isNewUser: false });
        }
      }
    } catch (error) {
      console.error("Login error:", error);
      res.status(400).json({ error: "Invalid request" });
    }
  });

  // ─── Auth: Request 2FA PIN ───
  app.post("/api/auth/request-pin", async (req: Request, res: Response) => {
    try {
      const { userId } = z.object({ userId: z.string() }).parse(req.body);
      const user = await storage.getUser(userId);

      if (!user || !user.phoneNumber) {
        return res.status(400).json({ error: "User not found or phone not set" });
      }

      const pin = generatePin();
      const expiresAt = Date.now() + 5 * 60 * 1000;
      pinStore.set(userId, { pin, expiresAt });

      console.log(`[2FA] PIN for ${user.phoneNumber}: ${pin}`);
      res.json({ success: true, pin, phoneNumber: user.phoneNumber });
    } catch (error) {
      res.status(400).json({ error: "Invalid request" });
    }
  });

  // ─── Auth: Verify 2FA PIN ───
  app.post("/api/auth/verify-pin", async (req: Request, res: Response) => {
    try {
      const { userId, pin } = z
        .object({ userId: z.string(), pin: z.string() })
        .parse(req.body);

      const stored = pinStore.get(userId);
      if (!stored || stored.pin !== pin || stored.expiresAt < Date.now()) {
        return res.status(401).json({ error: "Invalid or expired PIN" });
      }

      pinStore.delete(userId);
      await storage.updateUserLogin(userId);
      const user = await storage.getUser(userId);

      res.json({ user, verified: true });
    } catch (error) {
      res.status(400).json({ error: "Invalid request" });
    }
  });

  // ─── Auth: Complete onboarding ───
  app.post("/api/auth/complete-onboarding", async (req: Request, res: Response) => {
    try {
      const { userId, phoneNumber } = z
        .object({ userId: z.string(), phoneNumber: z.string() })
        .parse(req.body);

      await storage.updateUserOnboarding(userId, phoneNumber);

      const profile = await storage.getPlayerProfile(userId);
      if (profile) {
        await storage.updatePlayerProfile(profile.id, { status: "active" });
      }

      const user = await storage.getUser(userId);
      res.json({ user });
    } catch (error) {
      res.status(400).json({ error: "Invalid request" });
    }
  });

  // ─── Auth: Accept Terms & Conditions ───
  app.post("/api/auth/accept-terms", async (req: Request, res: Response) => {
    try {
      const { userId, signature } = z
        .object({ userId: z.string(), signature: z.string() })
        .parse(req.body);

      await db
        .update(users)
        .set({ tcAcceptedAt: new Date(), tcSignature: signature })
        .where(eq(users.id, userId));

      const user = await storage.getUser(userId);
      res.json({ user });
    } catch (error) {
      res.status(400).json({ error: "Invalid request" });
    }
  });

  // ─── Admin: Reset terms ───
  app.post("/api/admin/reset-terms", async (_req: Request, res: Response) => {
    try {
      const resetPlayerUserIds = [
        "player-barca-015",
        "player-barca-018",
        "player-david-004",
        "player-marcus-001",
        "player-barca-008",
      ];

      await db.update(users).set({ tcAcceptedAt: null, tcSignature: null });

      await db
        .update(users)
        .set({ onboardingComplete: false })
        .where(inArray(users.id, resetPlayerUserIds));

      await db
        .update(playerProfiles)
        .set({ status: "inactive" })
        .where(inArray(playerProfiles.userId, resetPlayerUserIds));

      await db.delete(consentGrants).where(inArray(consentGrants.userId, resetPlayerUserIds));

      res.json({ success: true, message: "Reset complete." });
    } catch (error) {
      res.status(500).json({ error: "Failed to reset" });
    }
  });

  // ─── Users ───
  app.get("/api/users/:userId", async (req: Request, res: Response) => {
    try {
      const userId = String(req.params.userId);
      const user = await storage.getUser(userId);
      if (!user) return res.status(404).json({ error: "User not found" });
      res.json({ user });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch user" });
    }
  });

  // ─── Player Profile ───
  app.get("/api/profile/:userId", async (req: Request, res: Response) => {
    try {
      const userId = String(req.params.userId);
      const profile = await storage.getPlayerProfile(userId);
      res.json({ profile });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch profile" });
    }
  });

  app.post("/api/profile", async (req: Request, res: Response) => {
    try {
      const data = insertPlayerProfileSchema.parse(req.body);
      const existing = await storage.getPlayerProfile(data.userId);
      let profile;
      if (existing) {
        profile = await storage.updatePlayerProfile(existing.id, data);
      } else {
        profile = await storage.createPlayerProfile(data);
      }
      res.json({ profile });
    } catch (error) {
      res.status(400).json({ error: "Invalid profile data" });
    }
  });

  // ─── Consent Grants ───
  app.get("/api/consent/:userId", async (req: Request, res: Response) => {
    try {
      const userId = String(req.params.userId);
      const grants = await storage.listConsentGrants(userId);
      res.json({ grants });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch consent grants" });
    }
  });

  app.post("/api/consent", async (req: Request, res: Response) => {
    try {
      const data = insertConsentGrantSchema.parse(req.body);
      const grant = await storage.createConsentGrant(data);

      let invitation = null;
      if (req.body.recipientEmail && data.granteeType === "external_recipient") {
        const token = crypto.randomBytes(32).toString("hex");
        const expiresAt = new Date(Date.now() + 3 * 24 * 60 * 60 * 1000);
        invitation = await storage.createInvitationToken({
          token,
          consentGrantId: grant.id,
          recipientEmail: req.body.recipientEmail,
          recipientName: data.granteeName,
          granteeOrg: data.granteeOrg || null,
          expiresAt,
        });
      }

      res.json({ grant, invitation });
    } catch (error) {
      console.error("Consent grant creation error:", error);
      res.status(400).json({ error: "Invalid consent grant data" });
    }
  });

  app.get("/api/invitation/:token", async (req: Request, res: Response) => {
    try {
      const invitation = await storage.getInvitationByToken(String(req.params.token));
      if (!invitation) return res.status(404).json({ error: "Invitation not found" });
      if (new Date(invitation.expiresAt) < new Date()) {
        return res.status(410).json({ error: "Invitation has expired" });
      }
      res.json({ invitation });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch invitation" });
    }
  });

  app.delete("/api/consent/:id", async (req: Request, res: Response) => {
    try {
      await storage.deleteConsentGrant(String(req.params.id));
      res.json({ success: true });
    } catch (error) {
      res.status(500).json({ error: "Failed to delete consent grant" });
    }
  });

  // ─── PCME Entries ───
  app.get("/api/pcme/:userId", async (req: Request, res: Response) => {
    try {
      const userId = String(req.params.userId);
      const entries = await storage.listPcmeEntries(userId);
      res.json({ entries });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch PCME entries" });
    }
  });

  app.get("/api/pcme/entry/:id", async (req: Request, res: Response) => {
    try {
      const entry = await storage.getPcmeEntry(String(req.params.id));
      if (!entry) return res.status(404).json({ error: "PCME entry not found" });
      res.json({ entry });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch PCME entry" });
    }
  });

  app.put("/api/pcme/:id", async (req: Request, res: Response) => {
    try {
      const body = { ...req.body };
      const dateFields = ["recordedAt", "scatDate", "termsAcceptedAt", "signedAt"] as const;
      for (const field of dateFields) {
        if (body[field] && typeof body[field] === "string") {
          body[field] = new Date(body[field]);
        }
      }
      const entry = await storage.updatePcmeEntry(String(req.params.id), body);
      res.json({ entry });
    } catch (error) {
      console.error("PCME update error:", error);
      res.status(400).json({ error: "Failed to update PCME entry" });
    }
  });

  app.post("/api/pcme", async (req: Request, res: Response) => {
    try {
      const body = { ...req.body };
      const dateFields = ["recordedAt", "scatDate", "termsAcceptedAt", "signedAt"] as const;
      for (const field of dateFields) {
        if (body[field] && typeof body[field] === "string") {
          body[field] = new Date(body[field]);
        }
      }
      const data = insertPcmeEntrySchema.parse(body);
      const entry = await storage.createPcmeEntry(data);

      const profile = await storage.getPlayerProfile(data.userId);
      if (profile) {
        await storage.updatePlayerProfile(profile.id, {
          pcmeStatus: "entered" as any,
          pcmeExpectedDate: null as any,
        });
      }

      res.json({ entry });
    } catch (error) {
      console.error("PCME create error:", error);
      res.status(400).json({ error: "Invalid PCME entry data" });
    }
  });

  // ─── Injury Cases ───
  app.get("/api/injuries/:userId", async (req: Request, res: Response) => {
    try {
      const userId = String(req.params.userId);
      const injuries = await storage.listInjuryCases(userId);
      res.json({ injuries });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch injury cases" });
    }
  });

  app.get("/api/injury/:id", async (req: Request, res: Response) => {
    try {
      const injury = await storage.getInjuryCase(String(req.params.id));
      if (!injury) return res.status(404).json({ error: "Injury case not found" });
      res.json({ injury });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch injury case" });
    }
  });

  app.post("/api/injuries", async (req: Request, res: Response) => {
    try {
      const data = insertInjuryCaseSchema.parse(req.body);
      const injury = await storage.createInjuryCase(data);
      res.json({ injury });
    } catch (error) {
      res.status(400).json({ error: "Invalid injury case data" });
    }
  });

  app.patch("/api/injury/:id", async (req: Request, res: Response) => {
    try {
      const id = String(req.params.id);
      const updates = z
        .object({
          status: z.enum(["open", "closed"]).optional(),
          notes: z.string().optional(),
          rtpStatus: z.enum(["not_started", "in_rehab", "light_training", "full_training", "cleared"]).optional(),
          estimatedReturnDate: z.string().nullable().optional(),
          clearedBy: z.string().nullable().optional(),
          clearedAt: z.string().nullable().optional(),
        })
        .parse(req.body);

      const processedUpdates: any = { ...updates };
      if (updates.clearedAt) {
        processedUpdates.clearedAt = new Date(updates.clearedAt);
      } else if (updates.clearedAt === null) {
        processedUpdates.clearedAt = null;
      }

      const injury = await storage.updateInjuryCase(id, processedUpdates);
      res.json({ injury });
    } catch (error) {
      res.status(400).json({ error: "Invalid injury update data" });
    }
  });

  app.delete("/api/injury/:id", async (req: Request, res: Response) => {
    try {
      await storage.deleteInjuryCase(String(req.params.id));
      res.json({ success: true });
    } catch (error) {
      res.status(500).json({ error: "Failed to delete injury case" });
    }
  });

  // ─── Training Sessions ───
  app.get("/api/training", async (_req: Request, res: Response) => {
    try {
      const sessions = await storage.listAllTrainingSessions();
      res.json({ sessions });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch training sessions" });
    }
  });

  app.post("/api/training", async (req: Request, res: Response) => {
    try {
      const data = insertTrainingSessionSchema.parse(req.body);
      const session = await storage.createTrainingSession(data);
      res.json({ session });
    } catch (error) {
      res.status(400).json({ error: "Invalid training session data" });
    }
  });

  app.delete("/api/training/:id", async (req: Request, res: Response) => {
    try {
      await storage.deleteTrainingSession(String(req.params.id));
      res.json({ success: true });
    } catch (error) {
      res.status(500).json({ error: "Failed to delete training session" });
    }
  });

  // ═══════════ DOCTOR ENDPOINTS ═══════════

  app.get("/api/doctor/players", async (_req: Request, res: Response) => {
    try {
      const players = await storage.listAllPlayers();
      res.json({ players });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch players" });
    }
  });

  app.get("/api/doctor/injuries", async (_req: Request, res: Response) => {
    try {
      const injuries = await storage.listAllInjuries();
      res.json({ injuries });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch injuries" });
    }
  });

  app.get("/api/doctor/pcme", async (_req: Request, res: Response) => {
    try {
      const entries = await storage.listAllPcmeEntries();
      res.json({ entries });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch PCME entries" });
    }
  });

  app.get("/api/doctor/training", async (_req: Request, res: Response) => {
    try {
      const sessions = await storage.listAllTrainingSessions();
      res.json({ sessions });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch training sessions" });
    }
  });

  app.get("/api/doctor/players/:userId", async (req: Request, res: Response) => {
    try {
      const userId = req.params.userId as string;
      const user = await storage.getUser(userId);
      if (!user) return res.status(404).json({ error: "Player not found" });
      const profile = await storage.getPlayerProfile(userId);
      const injuries = await storage.listInjuryCases(userId);
      const pcmeEntries = await storage.listPcmeEntries(userId);
      const trainingSessions = await storage.listAllTrainingSessions();
      res.json({ user, profile, injuries, pcmeEntries, trainingSessions });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch player details" });
    }
  });

  app.post("/api/doctor/players/:userId/invite", async (req: Request, res: Response) => {
    try {
      const userId = req.params.userId as string;
      const { email, phoneNumber } = z
        .object({ email: z.string().email(), phoneNumber: z.string() })
        .parse(req.body);

      const profile = await storage.getPlayerProfile(userId);
      if (!profile) return res.status(404).json({ error: "Player profile not found" });

      const updatedProfile = await storage.updatePlayerProfile(profile.id, { status: "pending_consent" });

      const user = await storage.getUser(userId);
      if (user) {
        await db.update(users).set({ email, phoneNumber }).where(eq(users.id, userId));
      }

      res.json({ success: true, profile: updatedProfile });
    } catch (error) {
      console.error("Invite player error:", error);
      res.status(500).json({ error: "Failed to invite player" });
    }
  });

  // ═══════════ INJURY NOTES ═══════════

  app.get("/api/injury/:injuryId/notes", async (req: Request, res: Response) => {
    try {
      const notes = await storage.listInjuryNotes(req.params.injuryId as string);
      res.json({ notes });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch injury notes" });
    }
  });

  app.post("/api/injury/:injuryId/notes", async (req: Request, res: Response) => {
    try {
      const injuryId = req.params.injuryId as string;
      const { authorId, intensity, soapNotes, attachments, rtpStatus, estimatedReturnDate } = z
        .object({
          authorId: z.string(),
          intensity: z.number().min(1).max(10),
          soapNotes: z.string().min(1),
          rtpStatus: z.enum(["not_started", "in_rehab", "light_training", "full_training", "cleared"]).optional(),
          estimatedReturnDate: z.string().nullable().optional(),
          attachments: z
            .array(z.object({ name: z.string(), url: z.string(), type: z.string(), size: z.number() }))
            .optional(),
        })
        .parse(req.body);

      const injury = await storage.getInjuryCase(injuryId);
      if (!injury) return res.status(404).json({ error: "Injury case not found" });

      const note = await storage.createInjuryNote({
        injuryCaseId: injuryId,
        authorId,
        intensity,
        soapNotes,
        rtpStatus: rtpStatus || null,
        estimatedReturnDate: estimatedReturnDate || null,
        attachments: attachments || null,
      });

      if (rtpStatus || estimatedReturnDate) {
        const updatePayload: any = {};
        if (rtpStatus) {
          updatePayload.rtpStatus = rtpStatus;
          if (rtpStatus === "cleared") {
            updatePayload.clearedBy = authorId;
            updatePayload.clearedAt = new Date();
          } else {
            updatePayload.clearedBy = null;
            updatePayload.clearedAt = null;
          }
        }
        if (estimatedReturnDate) {
          updatePayload.estimatedReturnDate = estimatedReturnDate;
        }
        await storage.updateInjuryCase(injuryId, updatePayload);
      }

      res.json({ note });
    } catch (error) {
      console.error("Create injury note error:", error);
      res.status(500).json({ error: "Failed to create injury note" });
    }
  });

  app.get("/api/injury/:injuryId/details", async (req: Request, res: Response) => {
    try {
      const injuryId = req.params.injuryId as string;
      const injury = await storage.getInjuryCase(injuryId);
      if (!injury) return res.status(404).json({ error: "Injury case not found" });
      const notes = await storage.listInjuryNotes(injuryId);
      const user = await storage.getUser(injury.userId);
      const profile = user ? await storage.getPlayerProfile(injury.userId) : null;
      res.json({ injury, notes, user, profile });
    } catch (error) {
      res.status(500).json({ error: "Failed to fetch injury details" });
    }
  });

  // ─── User Account ───
  app.delete("/api/user/:id", async (req: Request, res: Response) => {
    try {
      await storage.deleteUser(String(req.params.id));
      res.json({ success: true });
    } catch (error) {
      console.error("Delete user error:", error);
      res.status(500).json({ error: "Failed to delete user account" });
    }
  });

  // ─── Graph AI (stub for mobile) ───
  app.post("/api/graph-ai/chat", async (req: Request, res: Response) => {
    res.json({
      response: "Graph AI is not configured in this environment.",
      graphActions: [],
      conversationHistory: [],
    });
  });
}
