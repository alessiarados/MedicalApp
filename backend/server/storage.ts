import {
  type User,
  type InsertUser,
  type PlayerProfile,
  type InsertPlayerProfile,
  type ConsentGrant,
  type InsertConsentGrant,
  type PcmeEntry,
  type InsertPcmeEntry,
  type InjuryCase,
  type InsertInjuryCase,
  type TrainingSession,
  type InsertTrainingSession,
  type InjuryNote,
  type InsertInjuryNote,
  type InvitationToken,
  type InsertInvitationToken,
  users,
  playerProfiles,
  consentGrants,
  pcmeEntries,
  injuryCases,
  trainingSessions,
  injuryNotes,
  invitationTokens,
} from "../shared/schema.js";
import { db } from "../db/index.js";
import { eq, desc } from "drizzle-orm";

export class DbStorage {
  // Users
  async getUser(id: string): Promise<User | undefined> {
    const result = await db.select().from(users).where(eq(users.id, id)).limit(1);
    return result[0];
  }

  async getUserByEmail(email: string): Promise<User | undefined> {
    const result = await db.select().from(users).where(eq(users.email, email)).limit(1);
    return result[0];
  }

  async getUserByPhone(phoneNumber: string): Promise<User | undefined> {
    const result = await db.select().from(users).where(eq(users.phoneNumber, phoneNumber)).limit(1);
    return result[0];
  }

  async createUser(insertUser: InsertUser): Promise<User> {
    const result = await db.insert(users).values(insertUser).returning();
    return result[0];
  }

  async updateUserLogin(id: string): Promise<void> {
    await db.update(users).set({ lastLoginAt: new Date() }).where(eq(users.id, id));
  }

  async updateUserOnboarding(id: string, phoneNumber: string): Promise<void> {
    await db.update(users).set({ onboardingComplete: true, phoneNumber }).where(eq(users.id, id));
  }

  // Player Profiles
  async getPlayerProfile(userId: string): Promise<PlayerProfile | undefined> {
    const result = await db.select().from(playerProfiles).where(eq(playerProfiles.userId, userId)).limit(1);
    return result[0];
  }

  async createPlayerProfile(profile: InsertPlayerProfile): Promise<PlayerProfile> {
    const result = await db.insert(playerProfiles).values(profile).returning();
    return result[0];
  }

  async updatePlayerProfile(id: string, profile: Partial<InsertPlayerProfile>): Promise<PlayerProfile> {
    const result = await db
      .update(playerProfiles)
      .set({ ...profile, updatedAt: new Date() })
      .where(eq(playerProfiles.id, id))
      .returning();
    return result[0];
  }

  // Consent Grants
  async listConsentGrants(userId: string): Promise<ConsentGrant[]> {
    return await db.select().from(consentGrants).where(eq(consentGrants.userId, userId)).orderBy(desc(consentGrants.createdAt));
  }

  async createConsentGrant(grant: InsertConsentGrant): Promise<ConsentGrant> {
    const result = await db.insert(consentGrants).values(grant).returning();
    return result[0];
  }

  async deleteConsentGrant(id: string): Promise<void> {
    await db.delete(consentGrants).where(eq(consentGrants.id, id));
  }

  // PCME Entries
  async listPcmeEntries(userId: string): Promise<PcmeEntry[]> {
    return await db.select().from(pcmeEntries).where(eq(pcmeEntries.userId, userId)).orderBy(desc(pcmeEntries.recordedAt));
  }

  async getPcmeEntry(id: string): Promise<PcmeEntry | undefined> {
    const result = await db.select().from(pcmeEntries).where(eq(pcmeEntries.id, id)).limit(1);
    return result[0];
  }

  async createPcmeEntry(entry: InsertPcmeEntry): Promise<PcmeEntry> {
    const result = await db.insert(pcmeEntries).values(entry).returning();
    return result[0];
  }

  async updatePcmeEntry(id: string, data: Partial<InsertPcmeEntry>): Promise<PcmeEntry> {
    const result = await db.update(pcmeEntries).set({ ...data, updatedAt: new Date() }).where(eq(pcmeEntries.id, id)).returning();
    return result[0];
  }

  // Injury Cases
  async listInjuryCases(userId: string): Promise<InjuryCase[]> {
    return await db.select().from(injuryCases).where(eq(injuryCases.userId, userId)).orderBy(desc(injuryCases.createdAt));
  }

  async getInjuryCase(id: string): Promise<InjuryCase | undefined> {
    const result = await db.select().from(injuryCases).where(eq(injuryCases.id, id)).limit(1);
    return result[0];
  }

  async createInjuryCase(injury: InsertInjuryCase): Promise<InjuryCase> {
    const result = await db.insert(injuryCases).values(injury).returning();
    return result[0];
  }

  async updateInjuryCase(id: string, injury: Partial<InsertInjuryCase>): Promise<InjuryCase> {
    const result = await db
      .update(injuryCases)
      .set({ ...injury, updatedAt: new Date() })
      .where(eq(injuryCases.id, id))
      .returning();
    return result[0];
  }

  async deleteInjuryCase(id: string): Promise<void> {
    await db.delete(injuryCases).where(eq(injuryCases.id, id));
  }

  // Training Sessions
  async createTrainingSession(session: InsertTrainingSession): Promise<TrainingSession> {
    const result = await db.insert(trainingSessions).values(session).returning();
    return result[0];
  }

  async listAllTrainingSessions(): Promise<TrainingSession[]> {
    return await db.select().from(trainingSessions).orderBy(desc(trainingSessions.createdAt));
  }

  async deleteTrainingSession(id: string): Promise<void> {
    await db.delete(trainingSessions).where(eq(trainingSessions.id, id));
  }

  // Injury Notes
  async listInjuryNotes(injuryCaseId: string): Promise<InjuryNote[]> {
    return await db.select().from(injuryNotes).where(eq(injuryNotes.injuryCaseId, injuryCaseId)).orderBy(desc(injuryNotes.createdAt));
  }

  async createInjuryNote(note: InsertInjuryNote): Promise<InjuryNote> {
    const result = await db.insert(injuryNotes).values(note).returning();
    return result[0];
  }

  // Doctor-specific queries
  async listAllPlayers(): Promise<Array<{ user: User; profile: PlayerProfile | null }>> {
    const allUsers = await db.select().from(users).where(eq(users.role, "player"));
    const result: Array<{ user: User; profile: PlayerProfile | null }> = [];
    for (const user of allUsers) {
      const profiles = await db.select().from(playerProfiles).where(eq(playerProfiles.userId, user.id)).limit(1);
      result.push({ user, profile: profiles[0] || null });
    }
    return result;
  }

  async listInactivePlayers(): Promise<Array<{ user: User; profile: PlayerProfile }>> {
    const inactiveProfiles = await db.select().from(playerProfiles).where(eq(playerProfiles.status, "inactive"));
    const result: Array<{ user: User; profile: PlayerProfile }> = [];
    for (const profile of inactiveProfiles) {
      const userList = await db.select().from(users).where(eq(users.id, profile.userId)).limit(1);
      if (userList[0]) {
        result.push({ user: userList[0], profile });
      }
    }
    return result;
  }

  async listAllInjuries(): Promise<InjuryCase[]> {
    return await db.select().from(injuryCases).orderBy(desc(injuryCases.createdAt));
  }

  async listAllPcmeEntries(): Promise<PcmeEntry[]> {
    return await db.select().from(pcmeEntries).orderBy(desc(pcmeEntries.recordedAt));
  }

  // Invitation Tokens
  async createInvitationToken(token: InsertInvitationToken): Promise<InvitationToken> {
    const result = await db.insert(invitationTokens).values(token).returning();
    return result[0];
  }

  async getInvitationByToken(token: string): Promise<InvitationToken | undefined> {
    const result = await db.select().from(invitationTokens).where(eq(invitationTokens.token, token)).limit(1);
    return result[0];
  }

  async deleteUser(id: string): Promise<void> {
    await db.delete(users).where(eq(users.id, id));
  }
}

export const storage = new DbStorage();
