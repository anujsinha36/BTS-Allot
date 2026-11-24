package com.example.btsallot.presentation.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.domain.repository.DutyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DutyDetailsUiState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val duty: Duty? = null,
    val applications: List<DutyApplication> = emptyList()
)

@HiltViewModel
class DutyDetailsViewModel @Inject constructor(
    private val repository: DutyRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<DutyDetailsUiState>(DutyDetailsUiState())
    val uiState : StateFlow<DutyDetailsUiState> = _uiState.asStateFlow()

    private var applicationListenerJob: Job? = null

    //get duty details for a dutyId
    fun loadDuty(id: String){
        Log.d("APP_DEBUG", "ViewModel requested dutyId: '$id'")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            repository.getDutyById(id)
                .onSuccess { duty ->
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        duty = duty,
                        errorMessage = if (duty == null) "Duty not found" else null
                    )
                }
                .onFailure { e->
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        errorMessage = e.message ?: "Unable to load duty"
                    )
                }
        }

        repository.observeApplicationsForDuty(id)
            .onEach { applications ->
                Log.d("APP_DEBUG", "Room returned ${applications.size} applications for dutyId: '$id'")
                Log.d(
                    "APP_DEBUG",
                    "Room query dutyId='$id', count=${applications.size}, " +
                            "returnedDutyIds=${applications.map { it.dutyId }}"
                )
                _uiState.value = _uiState.value.copy(
                    applications = applications
                )
            }
            .catch { e->
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unable to load applications"
                )
            }
            .launchIn(viewModelScope)

    }

    fun realTimeApplicationSync() {
        if (applicationListenerJob?.isActive == true) {
            Log.d("APP_DEBUG", "Applications listener already active")
            return
        }

        Log.d("APP_DEBUG", "Starting applications listener")
        applicationListenerJob = repository.listenToApplications()
            .catch { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
            .launchIn(viewModelScope)
    }

}
