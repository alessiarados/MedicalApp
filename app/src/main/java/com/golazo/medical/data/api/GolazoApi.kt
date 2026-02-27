package com.golazo.medical.data.api

import com.golazo.medical.data.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface GolazoApi {

    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/request-pin")
    suspend fun requestPin(@Body request: PinRequest): PinResponse

    @POST("api/auth/verify-pin")
    suspend fun verifyPin(@Body request: VerifyPinRequest): VerifyPinResponse

    @POST("api/auth/complete-onboarding")
    suspend fun completeOnboarding(@Body request: OnboardingRequest): OnboardingResponse

    @POST("api/auth/accept-terms")
    suspend fun acceptTerms(@Body request: AcceptTermsRequest): AcceptTermsResponse

    @GET("api/auth/inactive-players")
    suspend fun getInactivePlayers(): InactivePlayersResponse

    // Users
    @GET("api/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): UserResponse

    // Profile
    @GET("api/profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: String): ProfileResponse

    @POST("api/profile")
    suspend fun createProfile(@Body request: ProfileCreateRequest): ProfileResponse

    // Consent
    @GET("api/consent/{userId}")
    suspend fun getConsents(@Path("userId") userId: String): ConsentListResponse

    @POST("api/consent")
    suspend fun createConsent(@Body request: ConsentCreateRequest): ConsentCreateResponse

    @DELETE("api/consent/{id}")
    suspend fun deleteConsent(@Path("id") id: String): DeleteResponse

    @GET("api/invitation/{token}")
    suspend fun getInvitation(@Path("token") token: String): InvitationResponse

    // PCME
    @GET("api/pcme/{userId}")
    suspend fun getPcmeEntries(@Path("userId") userId: String): PcmeListResponse

    @GET("api/pcme/entry/{id}")
    suspend fun getPcmeEntry(@Path("id") id: String): PcmeEntryResponse

    @POST("api/pcme")
    suspend fun createPcmeEntry(@Body entry: PcmeEntry): PcmeEntryResponse

    @PUT("api/pcme/{id}")
    suspend fun updatePcmeEntry(@Path("id") id: String, @Body entry: PcmeEntry): PcmeEntryResponse

    // Injuries
    @GET("api/injuries/{userId}")
    suspend fun getInjuries(@Path("userId") userId: String): InjuryListResponse

    @GET("api/injury/{id}")
    suspend fun getInjury(@Path("id") id: String): InjuryResponse

    @POST("api/injuries")
    suspend fun createInjury(@Body injury: InjuryCase): InjuryResponse

    @PATCH("api/injury/{id}")
    suspend fun updateInjury(@Path("id") id: String, @Body injury: InjuryCase): InjuryResponse

    @DELETE("api/injury/{id}")
    suspend fun deleteInjury(@Path("id") id: String): DeleteResponse

    // Injury Notes
    @GET("api/injury/{injuryId}/notes")
    suspend fun getInjuryNotes(@Path("injuryId") injuryId: String): InjuryNotesResponse

    @POST("api/injury/{injuryId}/notes")
    suspend fun createInjuryNote(
        @Path("injuryId") injuryId: String,
        @Body request: InjuryNoteCreateRequest
    ): InjuryNoteResponse

    // Training
    @GET("api/training")
    suspend fun getTrainingSessions(): TrainingListResponse

    @POST("api/training")
    suspend fun createTrainingSession(@Body request: TrainingCreateRequest): TrainingResponse

    @DELETE("api/training/{id}")
    suspend fun deleteTrainingSession(@Path("id") id: String): DeleteResponse

    // Doctor
    @GET("api/doctor/players")
    suspend fun getDoctorPlayers(): DoctorPlayersResponse

    @GET("api/doctor/players/{userId}")
    suspend fun getDoctorPlayerDetail(@Path("userId") userId: String): DoctorPlayerDetailResponse

    @POST("api/doctor/players/{userId}/invite")
    suspend fun invitePlayer(
        @Path("userId") userId: String,
        @Body request: InvitePlayerRequest
    ): InvitePlayerResponse

    @GET("api/doctor/injuries")
    suspend fun getDoctorInjuries(): InjuryListResponse

    @GET("api/doctor/pcme")
    suspend fun getDoctorPcme(): PcmeListResponse

    @GET("api/doctor/training")
    suspend fun getDoctorTraining(): TrainingListResponse

    // Intelligence
    @POST("api/graph-ai/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse

    // Transcription
    @Multipart
    @POST("api/transcribe")
    suspend fun transcribe(@Part file: MultipartBody.Part): TranscriptResponse

    // Notifications
    @GET("api/notifications")
    suspend fun getNotifications(): NotificationsResponse

    @GET("api/notifications/unread-count")
    suspend fun getUnreadNotificationCount(): UnreadCountResponse

    @POST("api/notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: String): SuccessResponse

    @POST("api/notifications/read-all")
    suspend fun markAllNotificationsAsRead(): SuccessResponse
}
