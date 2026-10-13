package com.example.btsallot.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btsallot.data.room.application.DutyApplicationEntity
import com.example.btsallot.domain.repository.DutyRepository
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.domain.model.DutyTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

data class DutyUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val duties: List<Duty> = emptyList(),
    val appliedDutyIds: Set<String> = emptySet()
)

@HiltViewModel
class DutyViewModel @Inject constructor(
    private val repository: DutyRepository
): ViewModel(){

    private val startMonth = YearMonth.now().minusMonths(6).atDay(1).toString() // e.g. "2024-09-01"
    private val endMonth = YearMonth.now().plusMonths(2).atEndOfMonth().toString() // e.g. "2025-05-31"

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _appliedDutyIds = MutableStateFlow<Set<String>>(emptySet())

    private var appliedDutyListenerUserId: String? = null

    private var appliedDutyIdsJob: Job? = null
    private var userApplicationsListenerJob: Job? = null
    private var dutyUpdatesListenerJob: Job? = null

    private val duties: StateFlow<List<Duty>> = repository.observeDutiesByDate(startMonth, endMonth)
        .onStart{
            _isLoading.value = true
            _errorMessage.value = null
        }
        .onEach {
            _isLoading.value = false
        }
        .catch { e->
            _isLoading.value = false
            _errorMessage.value = e.message ?: "Unable to load duties"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val uiState: StateFlow<DutyUiState> =
        combine(
            _isLoading,
            _errorMessage,
            duties,
            _appliedDutyIds
        ){isLoading, errorMessage, duties, appliedDutyIds ->
            DutyUiState(
                isLoading = isLoading,
                errorMessage = errorMessage,
                duties = duties,
                appliedDutyIds = appliedDutyIds
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DutyUiState()
        )
    fun createDuty(duty: Duty){
        viewModelScope.launch {
            repository.createDuty(duty)
                .onFailure { e->
                    _errorMessage.value = e.message ?: "Unable to create duty"
                }
        }
    }

    fun createTemplate(template: DutyTemplate){
        viewModelScope.launch {
            repository.createTemplate(template)
                .onFailure { e->
                    _errorMessage.value = e.message ?: "Unable to create template"
                }
        }
    }

    fun startRealTimeSync(){
        if(dutyUpdatesListenerJob?.isActive == true) return

        dutyUpdatesListenerJob = repository.listenToDutyUpdates()
            .catch { e->
                _errorMessage.value = e.message ?: "Unable to listen for duty updates"
            }
            .launchIn(viewModelScope)
        // listenToDutyUpdates already writes into Room, so collect is not required. stateIn works
    }

    fun createApplication(application: DutyApplication){
        viewModelScope.launch {
            repository.createDutyApplication(application)
                .onFailure { e->
                    _errorMessage.value = e.message ?: "Unable to apply for duty"
                }
        }
    }

    fun getAppliedDutyId(userID: String){
        if (
            appliedDutyListenerUserId == userID &&
            appliedDutyIdsJob?.isActive == true &&
            userApplicationsListenerJob?.isActive == true
        ) {
            return
        }
        appliedDutyIdsJob?.cancel()
        userApplicationsListenerJob?.cancel()

        appliedDutyListenerUserId = userID

        appliedDutyIdsJob = repository.getAppliedDutyIds(userID)
            .catch { e ->
                _errorMessage.value = e.message ?: "Unable to load applied duties"
            }
            .onEach { ids ->
                _appliedDutyIds.value = ids
            }
            .launchIn(viewModelScope)


        userApplicationsListenerJob = repository.listenToUserApplications(userID)
            .catch { e ->
                _errorMessage.value = e.message ?: "Unable to listen for applications"
            }
            .launchIn(viewModelScope)
    }

    fun retry() {
        _errorMessage.value = null
        startRealTimeSync()
    }
    fun clearError() {
        _errorMessage.value = null
    }
}

