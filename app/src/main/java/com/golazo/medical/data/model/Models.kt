package com.golazo.medical.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val role: String = "",
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("onboarding_complete") val onboardingComplete: Boolean = false,
    @SerialName("non_uefa") val nonUefa: Boolean = false,
    @SerialName("tc_accepted_at") val tcAcceptedAt: String? = null,
    @SerialName("tc_signature") val tcSignature: String? = null,
    @SerialName("last_login_at") val lastLoginAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class PlayerProfile(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    val nationality: String = "",
    val club: String = "",
    val dob: String = "",
    val position: String = "",
    val gender: String = "male",
    @SerialName("image_url") val imageUrl: String? = null,
    val location: String? = null,
    val status: String = "active",
    @SerialName("pcme_status") val pcmeStatus: String = "missing",
    @SerialName("pcme_expected_date") val pcmeExpectedDate: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class ConsentScope(
    val scope: String = "",
    val accessLevel: String = "read",
    val duration: String = "permanent",
    val expiresDays: Int? = null
)

@Serializable
data class ConsentGrant(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("grantee_type") val granteeType: String = "",
    @SerialName("grantee_name") val granteeName: String = "",
    @SerialName("grantee_org") val granteeOrg: String? = null,
    val scopes: List<ConsentScope> = emptyList(),
    @SerialName("is_default") val isDefault: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class InvitationToken(
    val id: String = "",
    val token: String = "",
    @SerialName("consent_grant_id") val consentGrantId: String = "",
    @SerialName("recipient_email") val recipientEmail: String = "",
    @SerialName("recipient_name") val recipientName: String = "",
    @SerialName("grantee_org") val granteeOrg: String? = null,
    @SerialName("expires_at") val expiresAt: String = "",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Prescription(
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val prescribedBy: String = ""
)

@Serializable
data class MedicalCondition(
    val condition: String = "",
    val notes: String = "",
    val recordedAt: String = ""
)

@Serializable
data class Disease(
    val disease: String = "",
    val status: String = "",
    val recordedAt: String = ""
)

@Serializable
data class VaccineRecord(
    val vaccine: String = "",
    val date: String = "",
    val batch: String = ""
)

@Serializable
data class Medication(
    val medication: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val startDate: String = ""
)

@Serializable
data class Attachment(
    val name: String = "",
    val url: String = "",
    val type: String = "",
    val size: Long = 0,
    val uploadedAt: String = ""
)

@Serializable
data class PcmeEntry(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("recorded_at") val recordedAt: String = "",
    @SerialName("recorded_by") val recordedBy: String? = null,
    @SerialName("blood_type") val bloodType: String = "unknown",
    @SerialName("scat_score") val scatScore: Int? = null,
    @SerialName("scat_date") val scatDate: String? = null,
    @SerialName("ecg_status") val ecgStatus: String? = null,
    @SerialName("echo_status") val echoStatus: String? = null,
    val height: String? = null,
    val weight: String? = null,
    val asthma: String? = null,
    @SerialName("hepatitis_b") val hepatitisB: String? = null,
    @SerialName("tetanus_status") val tetanusStatus: String? = null,
    @SerialName("last_vaccine_date") val lastVaccineDate: String? = null,
    val prescriptions: List<Prescription> = emptyList(),
    @SerialName("medical_conditions") val medicalConditions: List<MedicalCondition> = emptyList(),
    val diseases: List<Disease> = emptyList(),
    @SerialName("vaccine_passport") val vaccinePassport: List<VaccineRecord> = emptyList(),
    val allergies: String? = null,
    @SerialName("current_medications") val currentMedications: List<Medication> = emptyList(),
    val notes: String? = null,
    val attachments: List<Attachment> = emptyList(),
    @SerialName("terms_accepted") val termsAccepted: Boolean = false,
    @SerialName("terms_accepted_at") val termsAcceptedAt: String? = null,
    @SerialName("signature_data") val signatureData: String? = null,
    @SerialName("signed_at") val signedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class InjuryCase(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("injury_category") val injuryCategory: String? = null,
    @SerialName("injury_subcategory") val injurySubcategory: String? = null,
    @SerialName("body_area") val bodyArea: String = "",
    @SerialName("injury_type") val injuryType: String? = null,
    val mechanism: String = "",
    @SerialName("is_reinjury") val isReinjury: Boolean = false,
    val severity: String = "minor",
    @SerialName("injury_date") val injuryDate: String? = null,
    @SerialName("estimated_return_date") val estimatedReturnDate: String? = null,
    @SerialName("treatment_plan") val treatmentPlan: String? = null,
    val status: String = "open",
    @SerialName("rtp_status") val rtpStatus: String = "not_started",
    @SerialName("cleared_by") val clearedBy: String? = null,
    @SerialName("cleared_at") val clearedAt: String? = null,
    @SerialName("created_by") val createdBy: String = "player",
    val notes: String? = null,
    @SerialName("voice_transcript") val voiceTranscript: String? = null,
    @SerialName("attachment_filenames") val attachmentFilenames: List<String>? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class InjuryNote(
    val id: String = "",
    @SerialName("injury_case_id") val injuryCaseId: String = "",
    @SerialName("author_id") val authorId: String = "",
    val intensity: Int = 1,
    @SerialName("soap_notes") val soapNotes: String = "",
    @SerialName("rtp_status") val rtpStatus: String? = null,
    @SerialName("estimated_return_date") val estimatedReturnDate: String? = null,
    val attachments: List<Attachment> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class TrainingSession(
    val id: String = "",
    val date: String = "",
    val type: String = "",
    val title: String = "",
    val duration: Int = 0,
    val attendees: Int = 0,
    val notes: String? = null,
    @SerialName("time_of_day") val timeOfDay: String = "morning",
    val pitch: String? = null,
    val distance: Int? = null,
    @SerialName("play_ids") val playIds: List<String>? = null,
    @SerialName("created_at") val createdAt: String? = null
)
