package com.example.btsallot.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btsallot.data.room.application.DutyApplicationEntity
import com.example.btsallot.domain.repository.DutyRepository
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.domain.model.DutyTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DutyViewModel @Inject constructor(
    private val repository: DutyRepository
): ViewModel(){

    private val startMonth = YearMonth.now().minusMonths(6).atDay(1).toString() // e.g. "2024-09-01"
    private val endMonth = YearMonth.now().plusMonths(2).atEndOfMonth().toString() // e.g. "2025-05-31"

    val duties: StateFlow<List<Duty>> = repository.observeDutiesByDate(startMonth, endMonth)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()

        )

    private val _dutyID = MutableStateFlow<Set<String>>(emptySet())
    val dutyID: StateFlow<Set<String>> = _dutyID.asStateFlow()

    init{
        startRealTimeSync()
    }

    fun createDuty(duty: Duty){
        viewModelScope.launch {
            repository.createDuty(duty)
        }
    }

    fun createTemplate(template: DutyTemplate){
        viewModelScope.launch {
            repository.createTemplate(template)
        }
    }

    fun syncData(){
        viewModelScope.launch {
            repository.syncDuties()
        }
    }

    fun startRealTimeSync(){
        viewModelScope.launch {
            repository.listenToDutyUpdates().collect {
                // We don't even need to use the returned list here because
                // listenToDutyUpdates already writes into Room,
            }
        }
    }

    fun createApplication(application: DutyApplication){
        viewModelScope.launch {
            repository.createDutyApplication(application)
        }
    }

    fun getAppliedDutyId(userID: String){
        viewModelScope.launch {
            repository.getAppliedDutyIds(userID).collect { it->
                _dutyID.value = it
            }
        }
        repository.listenToUserApplications(userID).launchIn(viewModelScope)
    }
}