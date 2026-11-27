package com.example.btsallot.domain.repository

import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.domain.model.DutyTemplate
import kotlinx.coroutines.flow.Flow

interface DutyRepository {

    //create manual duties
   suspend fun createDuty(duty: Duty): Result<Unit>

   //create templates for duty scheduling
   suspend fun createTemplate(template: DutyTemplate): Result<Unit>

   // Syncs Firestore data into Room
   fun listenToDutyUpdates(startDate: String, endDate: String): Flow<List<Duty>>

   // Single Source of Truth for UI
   fun getAllDuties(): Flow<List<Duty>>
   fun observeDutiesByDate(start: String, end: String): Flow<List<Duty>>

   fun observeDutyById(dutyId: String): Flow<Duty?>

   suspend fun createDutyApplication(application: DutyApplication): Result<Unit>

   suspend fun deleteApplication(dutyId: String, userId: String): Result<Unit>

   suspend fun getDutyById(dutyId: String): Result<Duty?>
   fun getAppliedDutyIds(userId: String): Flow<Set<String>>

   fun listenToUserApplications(userId: String): Flow<List<DutyApplication>>

   fun listenToApplications(): Flow<List<DutyApplication>>

   fun observeApplicationsForDuty(dutyId: String): Flow<List<DutyApplication>>

   fun getAllApplications(): Flow<List<DutyApplication>>


   suspend fun deleteDuty(dutyId: String): Result<Unit>
   fun observeAppliedDuties(userId: String): Flow<List<Duty>>
}
