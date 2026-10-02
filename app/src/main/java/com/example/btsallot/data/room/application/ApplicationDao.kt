package com.example.btsallot.data.room.application

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {
    @Upsert
    suspend fun cacheApplications(application: DutyApplicationEntity)

    @Query("SELECT dutyId FROM duty_applications WHERE userId = :userID")
    fun observeAppliedDutyIds(userID: String): Flow<List<String>>

    @Query("DELETE FROM duty_applications WHERE id = :applicationId")
    suspend fun deleteApplicationById(applicationId: String)

    @Query("DELETE FROM duty_applications")
    suspend fun clearApplications()
// Call this on Logout to ensure if other user logs in they don't see stale data
}