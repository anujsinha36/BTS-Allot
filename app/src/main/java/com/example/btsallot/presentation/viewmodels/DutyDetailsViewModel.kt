package com.example.btsallot.presentation.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.repository.DutyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DutyDetailsUiState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val duty: Duty? = null
)

@HiltViewModel
class DutyDetailsViewModel @Inject constructor(
    private val repository: DutyRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<DutyDetailsUiState>(DutyDetailsUiState())
    val uiState : StateFlow<DutyDetailsUiState> = _uiState.asStateFlow()

    fun loadDuty(id: String){
        viewModelScope.launch {
            _uiState.value = DutyDetailsUiState(loading = true)
            repository.getDutyById(id)
                .onSuccess { duty ->
                    _uiState.value = DutyDetailsUiState(
                        duty = duty,
                        errorMessage = if (duty == null)"Duty not found" else null
                    )
                }
                .onFailure { e->
                    _uiState.value = DutyDetailsUiState(
                        errorMessage = e.message ?: "Unable to load duty"
                    )
                }
        }
    }

}
