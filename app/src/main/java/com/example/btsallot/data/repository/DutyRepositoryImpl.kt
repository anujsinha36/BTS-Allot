package com.example.btsallot.data.repository

import android.util.Log
import com.example.btsallot.data.mappers.toDomainDuty
import com.example.btsallot.data.mappers.toDutyApplicationEntity
import com.example.btsallot.data.mappers.toEntity
import com.example.btsallot.data.mappers.toFireStoreDuty
import com.example.btsallot.data.mappers.toFirestoreDutyApplication
import com.example.btsallot.data.mappers.toFirestoreDutyTemplate
import com.example.btsallot.domain.repository.DutyRepository
import com.example.btsallot.data.model.FirestoreDuty
import com.example.btsallot.data.model.FirestoreDutyApplication
import com.example.btsallot.data.room.application.ApplicationDao
import com.example.btsallot.data.room.duty.DutyDao
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.domain.model.DutyTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class DutyRepositoryImpl @Inject constructor(
    private val dutyDao: DutyDao,
    private val applicationDao: ApplicationDao,
    private val firestoreDB: FirebaseFirestore
    ): DutyRepository {

    override suspend fun createDuty(duty: Duty): Result<Unit> {
        return try {
            val docId = "${duty.date}_${duty.meetingName.replace(" ","_")}"
            val dutyWithID = duty.copy(id = docId)

            //Save to firestore
            firestoreDB.collection("duties").document(docId)
                .set(dutyWithID.toFireStoreDuty()).await()

            // 2. PROACTIVE CACHE: Save to Room immediately
            dutyDao.cacheDuties(listOf(dutyWithID.toEntity()))

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

    override fun listenToDutyUpdates(): Flow<List<Duty>> = callbackFlow {
        val dutyListenerRegistration = firestoreDB.collection("duties")
            .addSnapshotListener { snapshots, exception ->
                if (exception != null){
                    //shuts down flow stream, tells downstream flow collectors (like your ViewModel or UI)
                    // that the stream closed because of an error, allowing .catch { ... } or try-catch in the ViewModel to handle the error
                    close(exception)
                    //exits callback function so code below is not executed in case of error
                    return@addSnapshotListener
                }
                snapshots?.let{it->
                    val duties = it.toObjects(FirestoreDuty::class.java)
                    launch(Dispatchers.IO){
                        try {
                            dutyDao.cacheDuties(duties.map { it.toEntity() })
                        }catch (e: Exception){
                            Log.e("DutyRepo", "Failed to cache duties from Firestore", e)
                        }
                    }
                }
            }
        //Clean up listener when flow is cancelled
        awaitClose { dutyListenerRegistration.remove() }
    }

    override fun getAllDuties(): Flow<List<Duty>> {
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

    override suspend fun createDutyApplication(application: DutyApplication): Result<Unit> {
        val applicationID = "${application.dutyId}_${application.userId.replace(" ", "_")}"

        // STEP 1: Optimistic Local Update (Room) - FAST & OFFLINE READY
        try {
            applicationDao.cacheApplications(application.toDutyApplicationEntity(applicationID))
            Log.d("RepoDubg", "Application created and cached successfully")

            //OPTIMISTIC UPDATE: Increment count in Room for instant UI feedback
            dutyDao.getDutyById(application.dutyId)?.let { cachedDuty->
                val updatedDuty = cachedDuty.copy(
                    btsReservedCount = cachedDuty.btsReservedCount + 1
                )
                dutyDao.cacheDuties(listOf(updatedDuty))
            }

        }catch (e: Exception){
            return Result.failure(e)
        }

        // STEP 2: Sync to Firebase (Remote)
        return try {
            val applicationDoc = firestoreDB.collection("applications").document(applicationID)
            val applicationToFirestore = application.toFirestoreDutyApplication()
            applicationDoc.set(applicationToFirestore).await()

            // Surgical Update in Firestore: Increment the count: Using FieldValue.increment ensures the count is correct even if multiple people apply at once
            firestoreDB.collection("duties").document(application.dutyId)
                .update("btsReservedCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()

            Result.success(Unit)
        }
        catch (e: Exception){
            if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE){
                Log.e("RepoDubg","Offline: Application saved locally, queued for remote sync.")
                Result.success(Unit)
            }
            else{
                // Permanent error (e.g., PERMISSION_DENIED etc): Rollback Room changes!
                Log.e("RepoDebug", "Permanent error: Reverting local application. ${e.message}")
                revertLocalApplication(application, applicationID)
                Result.failure(e)
            }

        }
    }

    override fun getAppliedDutyIds(userId: String): Flow<Set<String>> {
        return applicationDao.observeAppliedDutyIds(userId).map { it.toSet() }
    }

    override fun listenToUserApplications(userId: String): Flow<List<DutyApplication>> = callbackFlow {
        val applicationListenerRegistration = firestoreDB.collection("applications")
            .whereEqualTo("userID", userId)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null){
                    close(exception)
                    return@addSnapshotListener
                }
                snapshots?.let { it->
                    val firestoreApplications = it.toObjects(FirestoreDutyApplication::class.java)
                    launch(Dispatchers.IO) {
                        try {
                            firestoreApplications.forEach {application ->
                                val applicationID= "${application.dutyId}_${application.userID.replace(" ", "_")}"
                                applicationDao.cacheApplications(application.toDutyApplicationEntity(applicationID))
                            }
                        }catch (e: Exception){
                            Log.e("DutyRepo", "Failed to cache user applications", e)
                        }
                    }
                }
            }
        awaitClose { applicationListenerRegistration.remove() }
    }

    private suspend fun revertLocalApplication(application: DutyApplication, applicationId: String) {
        try {
            // 1. Remove the application record from Room
            applicationDao.deleteApplicationById(applicationId)

            // 2. Rollback (decrement) the reserved count in Room
            dutyDao.getDutyById(application.dutyId)?.let { cachedDuty ->
                val updatedCount = (cachedDuty.btsReservedCount - 1).coerceAtLeast(0)
                val updatedDuty = cachedDuty.copy(btsReservedCount = updatedCount)
                dutyDao.cacheDuties(listOf(updatedDuty))
            }

            Log.d("RepoDebug", "Successfully reverted local application for duty: ${application.dutyId}")
        } catch (e: Exception) {
            Log.e("RepoDebug", "Failed to revert local application: ${e.message}")
        }
    }

}
