package com.golazo.medical.data.repository

import com.golazo.medical.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val userId: String get() = _currentUser.value?.id ?: ""
    val userRole: String get() = _currentUser.value?.role ?: ""
    val isPlayer: Boolean get() = userRole == "player"
    val isDoctor: Boolean get() = userRole == "doctor"
    val isNonUefa: Boolean get() = _currentUser.value?.nonUefa ?: false
    val isOnboardingComplete: Boolean get() = _currentUser.value?.onboardingComplete ?: false
    val hasAcceptedTerms: Boolean get() = _currentUser.value?.tcAcceptedAt != null

    fun setUser(user: User) {
        _currentUser.value = user
    }

    fun updateUser(user: User) {
        _currentUser.value = user
    }

    fun clear() {
        _currentUser.value = null
    }
}
