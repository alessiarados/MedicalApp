package com.golazo.medical.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golazo.medical.data.model.*
import com.golazo.medical.data.repository.GolazoRepository
import com.golazo.medical.data.repository.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NavigationTarget(val userId: String, val isNewUser: Boolean, val requires2FA: Boolean)

data class LoginState(
    val email: String = "",
    val password: String = "",
    val role: String = "player",
    val isRegistration: Boolean = false,
    val nonUefa: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateTo: NavigationTarget? = null
)

data class PinState(
    val pin: String = "",
    val expectedPin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val verified: Boolean = false
)

data class OnboardingState(
    val firstName: String = "",
    val lastName: String = "",
    val nationality: String = "",
    val club: String = "",
    val dob: String = "",
    val position: String = "",
    val phoneNumber: String = "",
    val currentStep: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val completed: Boolean = false
)

data class TermsState(
    val signature: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val accepted: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: GolazoRepository,
    val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _pinState = MutableStateFlow(PinState())
    val pinState: StateFlow<PinState> = _pinState.asStateFlow()

    private val _onboardingState = MutableStateFlow(OnboardingState())
    val onboardingState: StateFlow<OnboardingState> = _onboardingState.asStateFlow()

    private val _termsState = MutableStateFlow(TermsState())
    val termsState: StateFlow<TermsState> = _termsState.asStateFlow()

    fun updateEmail(v: String) { _loginState.update { it.copy(email = v) } }
    fun updatePassword(v: String) { _loginState.update { it.copy(password = v) } }
    fun updateRole(v: String) { _loginState.update { it.copy(role = v) } }
    fun updateNonUefa(v: Boolean) { _loginState.update { it.copy(nonUefa = v) } }
    fun toggleRegistration() { _loginState.update { it.copy(isRegistration = !it.isRegistration, error = null) } }
    fun clearNavigation() { _loginState.update { it.copy(navigateTo = null) } }

    fun login() {
        val s = _loginState.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _loginState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, error = null) }
            repository.login(
                LoginRequest(
                    email = s.email,
                    password = s.password,
                    role = s.role,
                    isRegistration = s.isRegistration,
                    nonUefa = s.nonUefa
                )
            ).onSuccess { resp ->
                resp.user?.let { user ->
                    sessionManager.setUser(user)
                    _loginState.update {
                        it.copy(
                            isLoading = false,
                            navigateTo = NavigationTarget(user.id, resp.isNewUser, resp.requires2FA)
                        )
                    }
                } ?: _loginState.update { it.copy(isLoading = false, error = "Login failed") }
            }.onFailure { e ->
                _loginState.update { it.copy(isLoading = false, error = e.message ?: "Network error") }
            }
        }
    }

    // PIN
    fun updatePin(v: String) {
        if (v.length <= 4) _pinState.update { it.copy(pin = v, error = null) }
    }

    fun requestPin(userId: String) {
        viewModelScope.launch {
            repository.requestPin(userId).onSuccess { resp ->
                _pinState.update { it.copy(expectedPin = resp.pin ?: "") }
            }
        }
    }

    fun verifyPin(userId: String) {
        val pin = _pinState.value.pin
        if (pin.length != 4) {
            _pinState.update { it.copy(error = "Enter 4-digit PIN") }
            return
        }
        viewModelScope.launch {
            _pinState.update { it.copy(isLoading = true, error = null) }
            repository.verifyPin(userId, pin).onSuccess { resp ->
                if (resp.verified) {
                    resp.user?.let { sessionManager.updateUser(it) }
                    _pinState.update { it.copy(isLoading = false, verified = true) }
                } else {
                    _pinState.update { it.copy(isLoading = false, error = "Invalid PIN") }
                }
            }.onFailure { e ->
                _pinState.update { it.copy(isLoading = false, error = e.message ?: "Verification failed") }
            }
        }
    }

    // Onboarding
    fun updateOnboardingField(field: String, value: String) {
        _onboardingState.update {
            when (field) {
                "firstName" -> it.copy(firstName = value)
                "lastName" -> it.copy(lastName = value)
                "nationality" -> it.copy(nationality = value)
                "club" -> it.copy(club = value)
                "dob" -> it.copy(dob = value)
                "position" -> it.copy(position = value)
                "phoneNumber" -> it.copy(phoneNumber = value)
                else -> it
            }
        }
    }

    fun nextOnboardingStep() {
        _onboardingState.update { it.copy(currentStep = it.currentStep + 1) }
    }

    fun prevOnboardingStep() {
        _onboardingState.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0)) }
    }

    fun completeOnboarding(userId: String) {
        val s = _onboardingState.value
        viewModelScope.launch {
            _onboardingState.update { it.copy(isLoading = true, error = null) }
            repository.createProfile(
                ProfileCreateRequest(
                    userId = userId,
                    firstName = s.firstName,
                    lastName = s.lastName,
                    nationality = s.nationality,
                    club = s.club,
                    dob = s.dob,
                    position = s.position
                )
            ).onSuccess {
                repository.completeOnboarding(userId, s.phoneNumber).onSuccess { resp ->
                    resp.user?.let { sessionManager.updateUser(it) }
                    _onboardingState.update { it.copy(isLoading = false, completed = true) }
                }.onFailure { e ->
                    _onboardingState.update { it.copy(isLoading = false, error = e.message) }
                }
            }.onFailure { e ->
                _onboardingState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // Terms
    fun updateSignature(v: String) { _termsState.update { it.copy(signature = v) } }

    fun acceptTerms(userId: String) {
        val sig = _termsState.value.signature
        if (sig.isBlank()) {
            _termsState.update { it.copy(error = "Please provide your signature") }
            return
        }
        viewModelScope.launch {
            _termsState.update { it.copy(isLoading = true, error = null) }
            repository.acceptTerms(userId, sig).onSuccess { resp ->
                resp.user?.let { sessionManager.updateUser(it) }
                _termsState.update { it.copy(isLoading = false, accepted = true) }
            }.onFailure { e ->
                _termsState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
