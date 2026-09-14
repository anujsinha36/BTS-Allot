package com.example.btsallot.data.repository

import android.util.Log
import com.example.btsallot.data.mappers.toDomainDuty
import com.example.btsallot.data.mappers.toEntity
import com.example.btsallot.data.mappers.toFireStoreDuty
import com.example.btsallot.data.mappers.toFirestoreDutyTemplate
import com.example.btsallot.domain.repository.DutyRepository
import com.example.btsallot.data.model.FirestoreDuty
import com.example.btsallot.data.room.DutyDao
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyTemplate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class DutyRepositoryImpl @Inject constructor(
    private val dutyDao: DutyDao,
    private val firestoreDB: FirebaseFirestore
    ): DutyRepository {

    override suspend fun createDuty(duty: Duty): Result<Unit> {
        return try {
            val docId = "${duty.date}_${duty.meetingName.replace(" ","_")}"

            val dutyDoc = firestoreDB.collection("duties").document(docId)

            val dutyWithID = duty.copy(id = docId).toFireStoreDuty()
            dutyDoc.set(dutyWithID).await()
//            // The calendar observes Room, so keep its source of truth current as
//            // soon as a duty has been saved remotely.
//            dutyDao.cacheDuties(listOf(duty.copy(id = docId).toEntity()))
            Log.d("RepoDubg","duty successful")

            Result.success(Unit)
        }
        catch (e: Exception){
            Log.e("RepoDubg",e.message.toString())
            Result.failure(e)

        }
    }

    override suspend fun createTemplate(template: DutyTemplate): Result<Unit> {
        return try {
            val templateID = "${template.dayOfWeek}_${template.meetingName.replace(" ", "_")}"
            val templateDoc = firestoreDB.collection("templates").document(templateID)

            //put a check here with date and title to avoid duplicate creation
            val templateWithID = template.copy(id = templateID).toFirestoreDutyTemplate()
            templateDoc.set(templateWithID).await()
            Log.d("RepoDubg","template successfully created")

            Result.success(Unit)
        }
        catch (e: Exception){
            Log.e("RepoDubg",e.message.toString())
            Result.failure(e)

        }
    }

    override suspend fun syncDuties(): Result<Unit> {
        return try {
            val duties = firestoreDB.collection("duties").get().await()
                .toObjects(FirestoreDuty::class.java)

           val entities = duties.map { it.toEntity() }
            dutyDao.cacheDuties(entities)
            Result.success(Unit)
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }

    override fun getCachedDuties(): Flow<List<Duty>> {
        return dutyDao.getAllDuties().map { entities ->
            entities.map { it.toDomainDuty() }
        }
    }

    override fun observeDutiesByDate(start: String, end: String
    ): Flow<List<Duty>> {
        return  dutyDao.observeDuties(start,end).map { entities ->
            entities.map { it.toDomainDuty() }
        }
    }


}
