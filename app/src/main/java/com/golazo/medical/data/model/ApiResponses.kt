package com.golazo.medical.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val role: String,
    val isRegistration: Boolean = false,
    val nonUefa: Boolean = false
)

@Serializable
data class LoginResponse(
    val user: User? = null,
    val requires2FA: Boolean = false,
    val isNewUser: Boolean = false
)

@Serializable
data class PinRequest(val userId: String)

@Serializable
data class PinResponse(
    val success: Boolean = false,
    val pin: String? = null,
    val phoneNumber: String? = null
)

@Serializable
data class VerifyPinRequest(val userId: String, val pin: String)

@Serializable
data class VerifyPinResponse(val user: User? = null, val verified: Boolean = false)

@Serializable
data class OnboardingRequest(val userId: String, val phoneNumber: String)

@Serializable
data class OnboardingResponse(val user: User? = null)

@Serializable
data class AcceptTermsRequest(val userId: String, val signature: String)

@Serializable
data class AcceptTermsResponse(val user: User? = null)

@Serializable
data class ProfileResponse(val profile: PlayerProfile? = null)

@Serializable
data class ProfileCreateRequest(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val nationality: String,
    val club: String,
    val dob: String,
    val position: String,
    val imageUrl: String? = null,
    val location: String? = null
)

@Serializable
data class ConsentListResponse(val grants: List<ConsentGrant> = emptyList())

@Serializable
data class ConsentCreateRequest(
    val userId: String,
    val granteeType: String,
    val granteeName: String,
    val granteeOrg: String? = null,
    val scopes: List<ConsentScope> = emptyList(),
    val isDefault: Boolean = false,
    val recipientEmail: String? = null
)

@Serializable
data class ConsentCreateResponse(
    val grant: ConsentGrant? = null,
    val invitation: InvitationToken? = null
)

@Serializable
data class DeleteResponse(val success: Boolean = false)

@Serializable
data class InvitationResponse(val invitation: InvitationToken? = null)

@Serializable
data class PcmeListResponse(val entries: List<PcmeEntry> = emptyList())

@Serializable
data class PcmeEntryResponse(val entry: PcmeEntry? = null)

@Serializable
data class InjuryListResponse(val injuries: List<InjuryCase> = emptyList())

@Serializable
data class InjuryResponse(val injury: InjuryCase? = null)

@Serializable
data class InjuryNotesResponse(val notes: List<InjuryNote> = emptyList())

@Serializable
data class InjuryNoteResponse(val note: InjuryNote? = null)

@Serializable
data class InjuryNoteCreateRequest(
    val authorId: String,
    val intensity: Int,
    val soapNotes: String,
    val rtpStatus: String? = null,
    val estimatedReturnDate: String? = null,
    val attachments: List<Attachment> = emptyList()
)

@Serializable
data class TrainingListResponse(val sessions: List<TrainingSession> = emptyList())

@Serializable
data class TrainingResponse(val session: TrainingSession? = null)

@Serializable
data class TrainingCreateRequest(
    val date: String,
    val type: String,
    val title: String,
    val duration: Int,
    val attendees: Int,
    val notes: String? = null,
    val timeOfDay: String = "morning",
    val pitch: String? = null,
    val distance: Int? = null,
    val playIds: List<String>? = null
)

@Serializable
data class DoctorPlayersResponse(
    val players: List<PlayerWithProfile> = emptyList()
)

@Serializable
data class PlayerWithProfile(
    val user: User? = null,
    val profile: PlayerProfile? = null
)

@Serializable
data class DoctorPlayerDetailResponse(
    val user: User? = null,
    val profile: PlayerProfile? = null,
    val injuries: List<InjuryCase> = emptyList(),
    val pcmeEntries: List<PcmeEntry> = emptyList(),
    val trainingSessions: List<TrainingSession> = emptyList()
)

@Serializable
data class InvitePlayerRequest(
    val email: String,
    val phoneNumber: String
)

@Serializable
data class InvitePlayerResponse(
    val success: Boolean = false,
    val profile: PlayerProfile? = null
)

@Serializable
data class ChatRequest(
    val message: String,
    val graphContext: String? = null,
    val conversationHistory: List<ChatMessage> = emptyList()
)

@Serializable
data class ChatMessage(
    val role: String = "",
    val content: String = ""
)

@Serializable
data class ChatResponse(
    val response: String = "",
    val graphActions: List<String> = emptyList(),
    val conversationHistory: List<ChatMessage> = emptyList()
)

@Serializable
data class TranscriptResponse(val transcript: String = "")

@Serializable
data class UserResponse(val user: User? = null)

@Serializable
data class InactivePlayersResponse(
    val players: List<PlayerWithProfile> = emptyList()
)
