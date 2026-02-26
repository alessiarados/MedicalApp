package com.golazo.medical.ui.player

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

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: GolazoRepository,
    val sessionManager: SessionManager
) : ViewModel() {

    private val _profile = MutableStateFlow<PlayerProfile?>(null)
    val profile: StateFlow<PlayerProfile?> = _profile.asStateFlow()

    private val _injuries = MutableStateFlow<List<InjuryCase>>(emptyList())
    val injuries: StateFlow<List<InjuryCase>> = _injuries.asStateFlow()

    private val _currentInjury = MutableStateFlow<InjuryCase?>(null)
    val currentInjury: StateFlow<InjuryCase?> = _currentInjury.asStateFlow()

    private val _injuryNotes = MutableStateFlow<List<InjuryNote>>(emptyList())
    val injuryNotes: StateFlow<List<InjuryNote>> = _injuryNotes.asStateFlow()

    private val _consents = MutableStateFlow<List<ConsentGrant>>(emptyList())
    val consents: StateFlow<List<ConsentGrant>> = _consents.asStateFlow()

    private val _pcmeEntries = MutableStateFlow<List<PcmeEntry>>(emptyList())
    val pcmeEntries: StateFlow<List<PcmeEntry>> = _pcmeEntries.asStateFlow()

    private val _currentPcme = MutableStateFlow<PcmeEntry?>(null)
    val currentPcme: StateFlow<PcmeEntry?> = _currentPcme.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            repository.getProfile(sessionManager.userId).onSuccess {
                _profile.value = it.profile
            }
        }
    }

    fun loadInjuries() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getInjuries(sessionManager.userId).onSuccess {
                _injuries.value = it.injuries
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun loadInjuryDetail(injuryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getInjury(injuryId).onSuccess {
                _currentInjury.value = it.injury
            }
            repository.getInjuryNotes(injuryId).onSuccess {
                _injuryNotes.value = it.notes
            }
            _isLoading.value = false
        }
    }

    fun createInjury(injury: InjuryCase, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createInjury(injury.copy(userId = sessionManager.userId)).onSuccess {
                loadInjuries()
                onSuccess()
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun addInjuryNote(injuryId: String, request: InjuryNoteCreateRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.createInjuryNote(injuryId, request).onSuccess {
                loadInjuryDetail(injuryId)
                onSuccess()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun loadConsents() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getConsents(sessionManager.userId).onSuccess {
                _consents.value = it.grants
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun createConsent(request: ConsentCreateRequest, onSuccess: (ConsentCreateResponse) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createConsent(request).onSuccess {
                onSuccess(it)
                loadConsents()
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun deleteConsent(id: String) {
        viewModelScope.launch {
            repository.deleteConsent(id).onSuccess {
                loadConsents()
            }
        }
    }

    fun loadPcmeEntries() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPcmeEntries(sessionManager.userId).onSuccess {
                _pcmeEntries.value = it.entries
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun loadPcmeDetail(entryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPcmeEntry(entryId).onSuccess {
                _currentPcme.value = it.entry
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }
}
