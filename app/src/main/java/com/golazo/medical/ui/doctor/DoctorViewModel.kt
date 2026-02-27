package com.golazo.medical.ui.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golazo.medical.data.PreferencesManager
import com.golazo.medical.data.model.*
import com.golazo.medical.data.repository.GolazoRepository
import com.golazo.medical.data.repository.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorViewModel @Inject constructor(
    private val repository: GolazoRepository,
    val sessionManager: SessionManager,
    val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _players = MutableStateFlow<List<PlayerWithProfile>>(emptyList())
    val players: StateFlow<List<PlayerWithProfile>> = _players.asStateFlow()

    private val _playerDetail = MutableStateFlow<DoctorPlayerDetailResponse?>(null)
    val playerDetail: StateFlow<DoctorPlayerDetailResponse?> = _playerDetail.asStateFlow()

    private val _injuries = MutableStateFlow<List<InjuryCase>>(emptyList())
    val injuries: StateFlow<List<InjuryCase>> = _injuries.asStateFlow()

    private val _currentInjury = MutableStateFlow<InjuryCase?>(null)
    val currentInjury: StateFlow<InjuryCase?> = _currentInjury.asStateFlow()

    private val _injuryNotes = MutableStateFlow<List<InjuryNote>>(emptyList())
    val injuryNotes: StateFlow<List<InjuryNote>> = _injuryNotes.asStateFlow()

    private val _pcmeEntries = MutableStateFlow<List<PcmeEntry>>(emptyList())
    val pcmeEntries: StateFlow<List<PcmeEntry>> = _pcmeEntries.asStateFlow()

    private val _currentPcme = MutableStateFlow<PcmeEntry?>(null)
    val currentPcme: StateFlow<PcmeEntry?> = _currentPcme.asStateFlow()

    private val _trainingSessions = MutableStateFlow<List<TrainingSession>>(emptyList())
    val trainingSessions: StateFlow<List<TrainingSession>> = _trainingSessions.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadPlayers() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDoctorPlayers().onSuccess {
                _players.value = it.players
            }.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun loadPlayerDetail(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDoctorPlayerDetail(userId).onSuccess {
                _playerDetail.value = it
            }.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun invitePlayer(userId: String, email: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.invitePlayer(userId, InvitePlayerRequest(email, phone)).onSuccess {
                onSuccess()
            }.onFailure { _error.value = it.message }
        }
    }

    fun loadInjuries() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDoctorInjuries().onSuccess {
                _injuries.value = it.injuries
            }.onFailure { _error.value = it.message }
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
            repository.createInjury(injury).onSuccess {
                onSuccess()
            }.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateInjury(id: String, injury: InjuryCase, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateInjury(id, injury).onSuccess {
                _currentInjury.value = it.injury
                onSuccess()
            }.onFailure { _error.value = it.message }
        }
    }

    fun addInjuryNote(injuryId: String, request: InjuryNoteCreateRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.createInjuryNote(injuryId, request).onSuccess {
                loadInjuryDetail(injuryId)
                onSuccess()
            }.onFailure { _error.value = it.message }
        }
    }

    fun loadPcmeEntries() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDoctorPcme().onSuccess {
                _pcmeEntries.value = it.entries
            }.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun loadPcmeDetail(entryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPcmeEntry(entryId).onSuccess {
                _currentPcme.value = it.entry
            }.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun loadPlayerPcmeHistory(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPcmeEntries(userId).onSuccess {
                _pcmeEntries.value = it.entries
            }.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun createPcmeEntry(entry: PcmeEntry, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createPcmeEntry(entry).onSuccess {
                onSuccess()
            }.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updatePcmeEntry(id: String, entry: PcmeEntry, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updatePcmeEntry(id, entry).onSuccess {
                _currentPcme.value = it.entry
                onSuccess()
            }.onFailure { _error.value = it.message }
        }
    }

    fun loadTrainingSessions() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDoctorTraining().onSuccess {
                _trainingSessions.value = it.sessions
            }.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun createTrainingSession(request: TrainingCreateRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.createTrainingSession(request).onSuccess {
                loadTrainingSessions()
                onSuccess()
            }.onFailure { _error.value = it.message }
        }
    }

    fun deleteTrainingSession(id: String) {
        viewModelScope.launch {
            repository.deleteTrainingSession(id).onSuccess {
                loadTrainingSessions()
            }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            repository.getNotifications().onSuccess {
                _notifications.value = it.notifications
            }
            repository.getUnreadNotificationCount().onSuccess {
                _unreadCount.value = it.count
            }
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id).onSuccess {
                loadNotifications()
            }
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead().onSuccess {
                _unreadCount.value = 0
                _notifications.value = _notifications.value.map { it.copy(isRead = true) }
            }
        }
    }
}
