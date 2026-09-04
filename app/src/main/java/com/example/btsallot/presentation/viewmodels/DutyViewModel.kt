package com.example.btsallot.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btsallot.data.DutyRepository
import com.example.btsallot.data.room.DutyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DutyViewModel @Inject constructor(
    private val repository: DutyRepository
): ViewModel(){

    var duties = mutableStateOf<List<DutyEntity>>(emptyList())
        private set

    init{
        syncData()
        getAllDuties()
    }
    fun syncData(){
        viewModelScope.launch {
            repository.syncDuties()
        }
    }

    fun getAllDuties(){
        viewModelScope.launch {
            repository.getCachedDuties().collect { cachedDuties->
                duties.value = cachedDuties
            }
        }
    }
}