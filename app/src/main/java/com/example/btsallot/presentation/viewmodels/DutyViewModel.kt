package com.example.btsallot.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btsallot.domain.repository.DutyRepository
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.domain.model.DutyTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DutyViewModel @Inject constructor(
    private val repository: DutyRepository
): ViewModel(){

    private val _duties = MutableStateFlow<List<Duty>>(emptyList())
    val duties: StateFlow<List<Duty>> = _duties.asStateFlow()

    init{
        getAllDuties()
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

    fun getAllDuties(){
        viewModelScope.launch {
            repository.getAllDuties().collect { cachedDuties->
                _duties.value = cachedDuties
            }
        }
    }

    fun createApplication(application: DutyApplication){
        viewModelScope.launch {
            repository.createDutyApplication(application)
        }
    }
}