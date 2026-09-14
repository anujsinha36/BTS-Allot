package com.example.btsallot.domain.repository

import com.example.btsallot.data.model.FirestoreDutyTemplate
import com.example.btsallot.data.model.FirestoreDuty
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyTemplate
import kotlinx.coroutines.flow.Flow

interface DutyRepository {

    //create manual duties
   suspend fun createDuty(duty: Duty): Result<Unit>

   //create templates for duty scheduling
   suspend fun createTemplate(template: DutyTemplate): Result<Unit>

   // Syncs Firestore data into Room
   suspend fun syncDuties(): Result<Unit>

   // Single Source of Truth for UI
   fun getCachedDuties(): Flow<List<Duty>>
   fun observeDutiesByDate(start: String, end: String): Flow<List<Duty>>
}