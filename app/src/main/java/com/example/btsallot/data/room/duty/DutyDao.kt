package com.example.btsallot.data.room.duty

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DutyDao {
    @Upsert
    suspend fun cacheDuties(duties: List<DutyEntity>)

    @Query("SELECT * FROM duties")
    fun getAllDuties(): Flow<List<DutyEntity>>

    @Query("SELECT * FROM duties WHERE date BETWEEN :startDate AND :endDate")
    fun observeDuties(startDate: String,endDate: String): Flow<List<DutyEntity>>
}