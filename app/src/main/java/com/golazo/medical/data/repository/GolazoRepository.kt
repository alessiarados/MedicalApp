package com.golazo.medical.data.repository

import com.golazo.medical.data.api.GolazoApi
import com.golazo.medical.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GolazoRepository @Inject constructor(
    private val api: GolazoApi
) {
    // Auth
    suspend fun login(request: LoginRequest) = runCatching { api.login(request) }
    suspend fun requestPin(userId: String) = runCatching { api.requestPin(PinRequest(userId)) }
    suspend fun verifyPin(userId: String, pin: String) = runCatching { api.verifyPin(VerifyPinRequest(userId, pin)) }
    suspend fun completeOnboarding(userId: String, phone: String) = runCatching { api.completeOnboarding(OnboardingRequest(userId, phone)) }
    suspend fun acceptTerms(userId: String, signature: String) = runCatching { api.acceptTerms(AcceptTermsRequest(userId, signature)) }
    suspend fun getInactivePlayers() = runCatching { api.getInactivePlayers() }

    // Users
    suspend fun getUser(userId: String) = runCatching { api.getUser(userId) }

    // Profile
    suspend fun getProfile(userId: String) = runCatching { api.getProfile(userId) }
    suspend fun createProfile(request: ProfileCreateRequest) = runCatching { api.createProfile(request) }

    // Consent
    suspend fun getConsents(userId: String) = runCatching { api.getConsents(userId) }
    suspend fun createConsent(request: ConsentCreateRequest) = runCatching { api.createConsent(request) }
    suspend fun deleteConsent(id: String) = runCatching { api.deleteConsent(id) }
    suspend fun getInvitation(token: String) = runCatching { api.getInvitation(token) }

    // PCME
    suspend fun getPcmeEntries(userId: String) = runCatching { api.getPcmeEntries(userId) }
    suspend fun getPcmeEntry(id: String) = runCatching { api.getPcmeEntry(id) }
    suspend fun createPcmeEntry(entry: PcmeEntry) = runCatching { api.createPcmeEntry(entry) }
    suspend fun updatePcmeEntry(id: String, entry: PcmeEntry) = runCatching { api.updatePcmeEntry(id, entry) }

    // Injuries
    suspend fun getInjuries(userId: String) = runCatching { api.getInjuries(userId) }
    suspend fun getInjury(id: String) = runCatching { api.getInjury(id) }
    suspend fun createInjury(injury: InjuryCase) = runCatching { api.createInjury(injury) }
    suspend fun updateInjury(id: String, injury: InjuryCase) = runCatching { api.updateInjury(id, injury) }
    suspend fun deleteInjury(id: String) = runCatching { api.deleteInjury(id) }

    // Injury Notes
    suspend fun getInjuryNotes(injuryId: String) = runCatching { api.getInjuryNotes(injuryId) }
    suspend fun createInjuryNote(injuryId: String, request: InjuryNoteCreateRequest) = runCatching { api.createInjuryNote(injuryId, request) }

    // Training
    suspend fun getTrainingSessions() = runCatching { api.getTrainingSessions() }
    suspend fun createTrainingSession(request: TrainingCreateRequest) = runCatching { api.createTrainingSession(request) }
    suspend fun deleteTrainingSession(id: String) = runCatching { api.deleteTrainingSession(id) }

    // Doctor
    suspend fun getDoctorPlayers() = runCatching { api.getDoctorPlayers() }
    suspend fun getDoctorPlayerDetail(userId: String) = runCatching { api.getDoctorPlayerDetail(userId) }
    suspend fun invitePlayer(userId: String, request: InvitePlayerRequest) = runCatching { api.invitePlayer(userId, request) }
    suspend fun getDoctorInjuries() = runCatching { api.getDoctorInjuries() }
    suspend fun getDoctorPcme() = runCatching { api.getDoctorPcme() }
    suspend fun getDoctorTraining() = runCatching { api.getDoctorTraining() }

    // Intelligence
    suspend fun chat(request: ChatRequest) = runCatching { api.chat(request) }
}
