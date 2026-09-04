package com.example.btsallot.data

import com.example.btsallot.data.model.Duty
import com.example.btsallot.data.room.DutyEntity
import kotlinx.coroutines.flow.Flow

interface DutyRepository {


   // suspend fun createDuty(duty: Duty): Result<Unit>
   // 1. Syncs Firestore data into Room
   suspend fun syncDuties(): Result<Unit>

    // 2. Single Source of Truth for UI
   fun getCachedDuties(): Flow<List<DutyEntity>>
   fun observeDutiesByDate(start: String, end: String): Flow<List<DutyEntity>>
}