import { sql } from "drizzle-orm";
import { pgTable, text, varchar, timestamp, boolean, jsonb, integer } from "drizzle-orm/pg-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

export const users = pgTable("users", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  email: text("email").notNull().unique(),
  password: text("password").notNull(),
  phoneNumber: text("phone_number"),
  role: text("role", { enum: ["player", "doctor"] }).notNull(),
  onboardingComplete: boolean("onboarding_complete").default(false),
  nonUefa: boolean("non_uefa").default(false),
  tcAcceptedAt: timestamp("tc_accepted_at"),
  tcSignature: text("tc_signature"),
  lastLoginAt: timestamp("last_login_at"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

export const insertUserSchema = createInsertSchema(users).omit({ id: true, createdAt: true });
export type InsertUser = z.infer<typeof insertUserSchema>;
export type User = typeof users.$inferSelect;

export const playerProfiles = pgTable("player_profiles", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  userId: varchar("user_id").notNull().references(() => users.id, { onDelete: "cascade" }),
  firstName: text("first_name").notNull(),
  lastName: text("last_name").notNull(),
  nationality: text("nationality").notNull(),
  club: text("club").notNull(),
  dob: text("dob").notNull(),
  position: text("position").notNull(),
  imageUrl: text("image_url"),
  location: text("location"),
  status: text("status", { enum: ["active", "inactive", "pending_consent"] }).default("active").notNull(),
  pcmeStatus: text("pcme_status", { enum: ["missing", "entered", "expected", "late"] }).default("missing").notNull(),
  pcmeExpectedDate: timestamp("pcme_expected_date"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export const insertPlayerProfileSchema = createInsertSchema(playerProfiles).omit({
  id: true,
  createdAt: true,
  updatedAt: true,
});
export type InsertPlayerProfile = z.infer<typeof insertPlayerProfileSchema>;
export type PlayerProfile = typeof playerProfiles.$inferSelect;

export type ScopeEntry = {
  scope: string;
  accessLevel: "read" | "edit";
  duration: "permanent" | "time_bound";
  expiresDays?: number;
};

export const consentGrants = pgTable("consent_grants", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  userId: varchar("user_id").notNull().references(() => users.id, { onDelete: "cascade" }),
  granteeType: text("grantee_type", {
    enum: ["national_team_doctor", "parent_guardian", "external_recipient"],
  }).notNull(),
  granteeName: text("grantee_name").notNull(),
  granteeOrg: text("grantee_org"),
  scopes: jsonb("scopes").$type<ScopeEntry[]>().notNull(),
  isDefault: boolean("is_default").default(false),
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

export const insertConsentGrantSchema = createInsertSchema(consentGrants).omit({
  id: true,
  createdAt: true,
});
export type InsertConsentGrant = z.infer<typeof insertConsentGrantSchema>;
export type ConsentGrant = typeof consentGrants.$inferSelect;

export const pcmeEntries = pgTable("pcme_entries", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  userId: varchar("user_id").notNull().references(() => users.id, { onDelete: "cascade" }),
  recordedAt: timestamp("recorded_at").notNull(),
  recordedBy: varchar("recorded_by").references(() => users.id),
  bloodType: text("blood_type", { enum: ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "unknown"] }),
  scatScore: integer("scat_score"),
  scatDate: timestamp("scat_date"),
  ecgStatus: text("ecg_status"),
  echoStatus: text("echo_status"),
  height: text("height"),
  weight: text("weight"),
  asthma: text("asthma"),
  hepatitisB: text("hepatitis_b"),
  tetanusStatus: text("tetanus_status"),
  lastVaccineDate: text("last_vaccine_date"),
  prescriptions: jsonb("prescriptions").$type<Array<{ name: string; dosage: string; frequency: string; prescribedBy: string }>>(),
  medicalConditions: jsonb("medical_conditions").$type<Array<{ condition: string; notes: string; recordedAt: string }>>(),
  diseases: jsonb("diseases").$type<Array<{ disease: string; status: string; recordedAt: string }>>(),
  vaccinePassport: jsonb("vaccine_passport").$type<Array<{ vaccine: string; date: string; batch: string }>>(),
  allergies: text("allergies"),
  currentMedications: jsonb("current_medications").$type<Array<{ medication: string; dosage: string; frequency: string; startDate: string }>>(),
  notes: text("notes"),
  attachments: jsonb("attachments").$type<Array<{ name: string; url: string; uploadedAt: string }>>(),
  termsAccepted: boolean("terms_accepted").default(false),
  termsAcceptedAt: timestamp("terms_accepted_at"),
  signatureData: text("signature_data"),
  signedAt: timestamp("signed_at"),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export const insertPcmeEntrySchema = createInsertSchema(pcmeEntries).omit({
  id: true,
  createdAt: true,
  updatedAt: true,
});
export type InsertPcmeEntry = z.infer<typeof insertPcmeEntrySchema>;
export type PcmeEntry = typeof pcmeEntries.$inferSelect;

export const invitationTokens = pgTable("invitation_tokens", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  token: text("token").notNull().unique(),
  consentGrantId: varchar("consent_grant_id").notNull().references(() => consentGrants.id, { onDelete: "cascade" }),
  recipientEmail: text("recipient_email").notNull(),
  recipientName: text("recipient_name").notNull(),
  granteeOrg: text("grantee_org"),
  expiresAt: timestamp("expires_at").notNull(),
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

export const insertInvitationTokenSchema = createInsertSchema(invitationTokens).omit({
  id: true,
  createdAt: true,
});
export type InsertInvitationToken = z.infer<typeof insertInvitationTokenSchema>;
export type InvitationToken = typeof invitationTokens.$inferSelect;

export const injuryCases = pgTable("injury_cases", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  userId: varchar("user_id").notNull().references(() => users.id, { onDelete: "cascade" }),
  injuryCategory: text("injury_category"),
  injurySubcategory: text("injury_subcategory"),
  bodyArea: text("body_area").notNull(),
  injuryType: text("injury_type"),
  mechanism: text("mechanism").notNull(),
  isReinjury: boolean("is_reinjury").default(false),
  severity: text("severity", { enum: ["minor", "moderate", "severe"] }).notNull(),
  injuryDate: text("injury_date"),
  estimatedReturnDate: text("estimated_return_date"),
  treatmentPlan: text("treatment_plan"),
  status: text("status", { enum: ["open", "closed"] }).notNull(),
  rtpStatus: text("rtp_status", { enum: ["not_started", "in_rehab", "light_training", "full_training", "cleared"] }).default("not_started"),
  clearedBy: text("cleared_by"),
  clearedAt: timestamp("cleared_at"),
  createdBy: text("created_by", { enum: ["player", "doctor", "parent"] }).notNull(),
  notes: text("notes"),
  voiceTranscript: text("voice_transcript"),
  attachmentFilenames: jsonb("attachment_filenames").$type<string[]>(),
  createdAt: timestamp("created_at").defaultNow().notNull(),
  updatedAt: timestamp("updated_at").defaultNow().notNull(),
});

export const insertInjuryCaseSchema = createInsertSchema(injuryCases).omit({
  id: true,
  createdAt: true,
  updatedAt: true,
});
export type InsertInjuryCase = z.infer<typeof insertInjuryCaseSchema>;
export type InjuryCase = typeof injuryCases.$inferSelect;

export const trainingSessions = pgTable("training_sessions", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  date: text("date").notNull(),
  type: text("type", { enum: ["practice", "weights", "film", "conditioning", "recovery"] }).notNull(),
  title: text("title").notNull(),
  duration: integer("duration").notNull(),
  attendees: integer("attendees").notNull(),
  notes: text("notes"),
  timeOfDay: text("time_of_day", { enum: ["morning", "afternoon", "evening"] }),
  pitch: text("pitch"),
  distance: integer("distance"),
  playIds: jsonb("play_ids").$type<string[]>(),
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

export const insertTrainingSessionSchema = createInsertSchema(trainingSessions).omit({
  id: true,
  createdAt: true,
});
export type InsertTrainingSession = z.infer<typeof insertTrainingSessionSchema>;
export type TrainingSession = typeof trainingSessions.$inferSelect;

export const injuryNotes = pgTable("injury_notes", {
  id: varchar("id").primaryKey().default(sql`gen_random_uuid()`),
  injuryCaseId: varchar("injury_case_id").notNull().references(() => injuryCases.id, { onDelete: "cascade" }),
  authorId: varchar("author_id").notNull().references(() => users.id),
  intensity: integer("intensity").notNull(),
  soapNotes: text("soap_notes").notNull(),
  rtpStatus: text("rtp_status", { enum: ["not_started", "in_rehab", "light_training", "full_training", "cleared"] }),
  estimatedReturnDate: text("estimated_return_date"),
  attachments: jsonb("attachments").$type<Array<{ name: string; url: string; type: string; size: number }>>(),
  createdAt: timestamp("created_at").defaultNow().notNull(),
});

export const insertInjuryNoteSchema = createInsertSchema(injuryNotes).omit({
  id: true,
  createdAt: true,
});
export type InsertInjuryNote = z.infer<typeof insertInjuryNoteSchema>;
export type InjuryNote = typeof injuryNotes.$inferSelect;
