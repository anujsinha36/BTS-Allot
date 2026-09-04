package com.example.btsallot.data.repository

import android.util.Log
import com.example.btsallot.data.DutyRepository
import com.example.btsallot.data.model.Duty
import com.example.btsallot.data.room.DutyDao
import com.example.btsallot.data.room.DutyEntity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class DutyRepositoryImpl @Inject constructor(
    private val dutyDao: DutyDao,
    private val firestoreDB: FirebaseFirestore
    ): DutyRepository {

    override suspend fun syncDuties(): Result<Unit> {
        return try {
            val duties = firestoreDB.collection("duties").get().await()
                .toObjects(Duty::class.java)

           val entities = duties.map { it.toEntity() }
            dutyDao.cacheDuties(entities)
            Result.success(Unit)
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }

    override fun getCachedDuties(): Flow<List<DutyEntity>> {
        return dutyDao.getAllDuties()
    }

    override fun observeDutiesByDate(
        start: String,
        end: String
    ): Flow<List<DutyEntity>> {
        return  dutyDao.observeDuties(start,end)
    }

    private fun Duty.toEntity() = DutyEntity(
        id = id,
        date = date,
        meetingName = duty.meetingName,
        startMinutes = duty.startMinutes,
        endMinutes = duty.endMinutes,
        btsRequired = duty.btsRequired,
        location = duty.location,
        notes = duty.notes
    )


}

//get data from firestore from getDuties() from auth repository to this repository and store in cacheDuties