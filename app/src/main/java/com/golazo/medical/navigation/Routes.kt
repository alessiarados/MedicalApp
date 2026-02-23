package com.golazo.medical.navigation

object Routes {
    // Auth
    const val LOGIN = "login"
    const val PIN_VERIFY = "pin_verify/{userId}"
    const val ONBOARDING = "onboarding/{userId}"
    const val TERMS = "terms/{userId}"

    // Player
    const val PLAYER_HOME = "player/home"
    const val PLAYER_INJURIES = "player/injuries"
    const val PLAYER_INJURY_CREATE = "player/injuries/create"
    const val PLAYER_INJURY_DETAIL = "player/injuries/{injuryId}"
    const val PLAYER_INJURY_HISTORY = "player/injuries/history"
    const val PLAYER_WELLBEING = "player/wellbeing"
    const val PLAYER_FIND_HELP = "player/wellbeing/find_help"
    const val PLAYER_SESSIONS = "player/wellbeing/sessions"
    const val PLAYER_SESSION_DETAIL = "player/wellbeing/sessions/{sessionId}"
    const val PLAYER_BREATHING = "player/wellbeing/breathing"
    const val PLAYER_PCME = "player/pcme"
    const val PLAYER_PCME_DETAIL = "player/pcme/{entryId}"
    const val PLAYER_CONSENT = "player/consent"
    const val PLAYER_CONSENT_CREATE = "player/consent/create"
    const val PLAYER_CONSENT_INVITE = "player/consent/invite/{token}"
    const val PLAYER_PROFILE = "player/profile"

    // Doctor
    const val DOCTOR_HOME = "doctor/home"
    const val DOCTOR_PLAYERS = "doctor/players"
    const val DOCTOR_PLAYER_DETAIL = "doctor/players/{userId}"
    const val DOCTOR_INJURIES = "doctor/injuries"
    const val DOCTOR_INJURY_DETAIL = "doctor/injuries/{injuryId}"
    const val DOCTOR_INJURY_CREATE = "doctor/injuries/create"
    const val DOCTOR_PCME = "doctor/pcme"
    const val DOCTOR_PCME_FORM = "doctor/pcme/form/{userId}"
    const val DOCTOR_PCME_DETAIL = "doctor/pcme/{entryId}"
    const val DOCTOR_PCME_HISTORY = "doctor/pcme/history/{userId}"
    const val DOCTOR_TRAINING = "doctor/training"
    const val DOCTOR_PLAYBOOK = "doctor/playbook"
    const val DOCTOR_SETTINGS = "doctor/settings"

    // Shared
    const val INTELLIGENCE = "intelligence"
    const val SIMULATIONS = "simulations"

    fun pinVerify(userId: String) = "pin_verify/$userId"
    fun onboarding(userId: String) = "onboarding/$userId"
    fun terms(userId: String) = "terms/$userId"
    fun playerInjuryDetail(injuryId: String) = "player/injuries/$injuryId"
    fun playerPcmeDetail(entryId: String) = "player/pcme/$entryId"
    fun playerConsentInvite(token: String) = "player/consent/invite/$token"
    fun playerSessionDetail(sessionId: String) = "player/wellbeing/sessions/$sessionId"
    fun doctorPlayerDetail(userId: String) = "doctor/players/$userId"
    fun doctorInjuryDetail(injuryId: String) = "doctor/injuries/$injuryId"
    fun doctorPcmeForm(userId: String) = "doctor/pcme/form/$userId"
    fun doctorPcmeDetail(entryId: String) = "doctor/pcme/$entryId"
    fun doctorPcmeHistory(userId: String) = "doctor/pcme/history/$userId"
}
