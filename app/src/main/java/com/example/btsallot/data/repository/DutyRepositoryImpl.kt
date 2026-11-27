package com.example.btsallot.data.repository

import android.util.Log
import com.example.btsallot.data.mappers.toDomainDuty
import com.example.btsallot.data.mappers.toDutyApplication
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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds


class DutyRepositoryImpl @Inject constructor(
    private val dutyDao: DutyDao,
    private val applicationDao: ApplicationDao,
    private val firestoreDB: FirebaseFirestore
) : DutyRepository {

    //Duties
    override suspend fun createDuty(duty: Duty): Result<Unit> {
        return try {
            val docId = "${duty.date}_${duty.meetingName.replace(" ", "_")}"
            val dutyWithID = duty.copy(id = docId)

            //Save to firestore
            firestoreDB.collection("duties").document(docId)
                .set(dutyWithID.toFireStoreDuty()).await()

            // 2. PROACTIVE CACHE: Save to Room immediately
            dutyDao.cacheDuties(listOf(dutyWithID.toEntity()))

            Log.d("RepoDubg", "duty successful")

            Result.success(Unit)
        }  catch (e: CancellationException) {
            throw e
        }
        catch (e: Exception) {
            Log.e("RepoDubg", e.message.toString())
            Result.failure(e)
        }
    }

    override suspend fun createTemplate(template: DutyTemplate): Result<Unit> {
        return try {
            val templateID = "${template.dayOfWeek}_${template.meetingName.replace(" ", "_")}"
            val templateDoc = firestoreDB.collection("templates").document(templateID)

            val templateWithID = template.copy(id = templateID).toFirestoreDutyTemplate()
            templateDoc.set(templateWithID).await()
            Log.d("RepoDubg", "template successfully created")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("RepoDubg", e.message.toString())
            Result.failure(e)

        }
    }

    //listener for firestore duties - also updating room
    override fun listenToDutyUpdates(startDate: String, endDate: String): Flow<List<Duty>> =
        callbackFlow {
            val dutyListenerRegistration = firestoreDB.collection("duties")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .addSnapshotListener { snapshots, exception ->
                    if (exception != null) {
                        //shuts down flow stream, tells downstream flow collectors (like your ViewModel or UI)
                        // that the stream closed because of an error, allowing .catch { ... } or try-catch in the ViewModel to handle the error
                        close(exception)
                        //exits callback function so code below is not executed in case of error
                        return@addSnapshotListener
                    }
                    snapshots?.let { snapshot ->
                        launch(Dispatchers.IO) {
                            try {
                                snapshot.documentChanges.forEach { change ->
                                    when(change.type){
                                        DocumentChange.Type.REMOVED -> {
                                            dutyDao.deleteDutyById(change.document.id)
                                        }

                                        DocumentChange.Type.ADDED,
                                        DocumentChange.Type.MODIFIED -> {
                                            val duty = change.document.toObject(FirestoreDuty::class.java)
                                                .toEntity()
                                            dutyDao.cacheDuties(listOf(duty))
                                        }
                                    }
                                }
                            }catch (e: Exception){
                                Log.e("DutyRepo", "Failed to sync duty changes", e)
                            }
                        }
                    }
                }
            //Clean up listener when flow is canceled
            awaitClose { dutyListenerRegistration.remove() }
        }

    override fun getAllDuties(): Flow<List<Duty>> {
        return dutyDao.getAllDuties().map { entities ->
            entities.map { it.toDomainDuty() }
        }
    }

    //fetch duties from Room
    override fun observeDutiesByDate(
        start: String, end: String
    ): Flow<List<Duty>> {
        return dutyDao.observeDuties(start, end).map { entities ->
            entities.map { it.toDomainDuty() }
        }
    }

    override fun observeAppliedDuties(userId: String): Flow<List<Duty>> {
        return dutyDao.observeAppliedDuties(userId).map { entities ->
            entities.map { it.toDomainDuty() }
        }
    }

    override fun observeDutyById(dutyId: String): Flow<Duty?> {
        return dutyDao.observeDutyById(dutyId).map { entity ->
            entity?.toDomainDuty()
        }
    }

    override suspend fun getDutyById(dutyId: String): Result<Duty?> {
        return try {
            val duty = dutyDao.getDutyById(dutyId)?.toDomainDuty()
            Result.success(duty)
        } catch (e: CancellationException){
            throw e
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun deleteDuty(dutyId: String): Result<Unit> {
        return try {
            // Delete remotely first, so a failed Firestore request does not remove
            // the only cached copy of the duty.
            firestoreDB.collection("duties").document(dutyId).delete().await()
            dutyDao.deleteDutyById(dutyId)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("DutyRepo", "Failed to delete duty from Firestore", e)
            Result.failure(e)
        }
    }


    //Applications
    override suspend fun createDutyApplication(application: DutyApplication): Result<Unit> {
        val applicationID = "${application.dutyId}_${application.userId.replace(" ", "_")}"

        // STEP 1: Optimistic Local Update (Room) - FAST & OFFLINE READY
        try {
            applicationDao.cacheApplications(application.toDutyApplicationEntity(applicationID))
            Log.d("RepoDubg", "Application created and cached successfully")

            //OPTIMISTIC UPDATE: Increment count in Room for instant UI feedback
            dutyDao.getDutyById(application.dutyId)?.let { cachedDuty ->
                val updatedDuty = cachedDuty.copy(
                    btsReservedCount = cachedDuty.btsReservedCount + 1
                )
                dutyDao.cacheDuties(listOf(updatedDuty))
            }

        } catch (e: Exception) {
            return Result.failure(e)
        }

        // STEP 2: Sync to Firebase (Remote)
        return try {
            val applicationDoc = firestoreDB.collection("applications").document(applicationID)
            val applicationToFirestore = application.toFirestoreDutyApplication()

            val dutyDoc = firestoreDB.collection("duties").document(application.dutyId)

            //create batch to ensure both firebase operations happen together
            val batch = firestoreDB.batch()
            batch.set(applicationDoc, applicationToFirestore)
            batch.update(dutyDoc, "btsReservedCount",
                FieldValue.increment(1))

            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        }
        catch (e: Exception) {
            revertLocalApplication(application, applicationID)
            Result.failure(e)
        }
    }

    override fun getAppliedDutyIds(userId: String): Flow<Set<String>> {
        return applicationDao.observeAppliedDutyIds(userId).map { it.toSet() }
    }

    override suspend fun deleteApplication(dutyId: String, userId: String): Result<Unit> {
        val applicationID =  "${dutyId}_${userId.replace(" ", "_")}"

        return try {

            // 1. STEP 1: Sync to Firestore FIRST with a 5-second timeout
            withTimeout(5000L.milliseconds){
                val applicationDoc = firestoreDB.collection("applications")
                    .document(applicationID)

                val dutyDoc =  firestoreDB.collection("duties").document(dutyId)

                val batch = firestoreDB.batch()
                batch.delete(applicationDoc)
                batch.update(dutyDoc, "btsReservedCount", FieldValue.increment(-1))
                batch.commit().await()
            }

            // 2. STEP 2: Remote call succeeded -> Update local Room DB
            applicationDao.deleteApplicationById(applicationID)
            dutyDao.getDutyById(dutyId)?.let { cachedDuty ->
                val updatedDuty = cachedDuty.copy(
                    btsReservedCount = (cachedDuty.btsReservedCount - 1).coerceAtLeast(0)
                )
                dutyDao.cacheDuties(listOf(updatedDuty))
            }
            Result.success(Unit)
        }
        catch (e: CancellationException){
            throw e
        }
        catch (e: Exception){
            Log.e("DutyRepo", "Failed to delete application from Firestore", e)
            Result.failure(e)
        }
    }

    //listener for firestore applications and updating room
    override fun listenToUserApplications(userId: String): Flow<List<DutyApplication>> =
        callbackFlow {
            val applicationListenerRegistration = firestoreDB.collection("applications")
                .whereEqualTo("userID", userId)
                .addSnapshotListener { snapshots, exception ->
                    if (exception != null) {
                        close(exception)
                        return@addSnapshotListener
                    }
                    snapshots?.let { snapshot ->
                        launch(Dispatchers.IO) {
                            try {
                                snapshot.documentChanges.forEach { change ->
                                    val firestoreApplication = change.document
                                        .toObject(FirestoreDutyApplication::class.java)

                                    //val applicationId = "${firestoreApplication.dutyId}_${firestoreApplication.userID.replace(" ", "_")}"
                                    val applicationId = change.document.id

                                    when (change.type) {
                                        DocumentChange.Type.REMOVED -> {
                                            applicationDao.deleteApplicationById(applicationId)
                                        }

                                        DocumentChange.Type.ADDED,
                                        DocumentChange.Type.MODIFIED -> {
                                            applicationDao.cacheApplications(
                                                firestoreApplication.toDutyApplicationEntity(applicationId)
                                            )
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("DutyRepo", "Failed to sync user application changes", e)
                            }
                        }
                    }
                }
            awaitClose { applicationListenerRegistration.remove() }
        }


    override fun observeApplicationsForDuty(dutyId: String): Flow<List<DutyApplication>> {
        return applicationDao.observeApplicationsForDuty(dutyId).map { entities ->
            entities.map { it.toDutyApplication()}
        }
    }

    override fun getAllApplications(): Flow<List<DutyApplication>> {
        return applicationDao.getAllApplications().map { entities ->
            entities.map { it.toDutyApplication() }
        }
    }

    override fun listenToApplications(): Flow<List<DutyApplication>> =
        callbackFlow {
            val applicationListenerRegistration = firestoreDB.collection("applications")
                .addSnapshotListener { snapshots, exception ->
                    if (exception != null) {
                        Log.e("APP_DEBUG", "Applications listener failed", exception)
                        close(exception)
                        return@addSnapshotListener
                    }
                    snapshots?.let { snapshot ->
                        launch(Dispatchers.IO) {
                            try {
                                snapshot.documentChanges.forEach { change ->
                                    val application = change.document
                                        .toObject(FirestoreDutyApplication::class.java)
                                    //val applicationId = "${application.dutyId}_${application.userID.replace(" ", "_")}"
                                    val applicationId = change.document.id

                                    when (change.type) {
                                        DocumentChange.Type.REMOVED -> {
                                            applicationDao.deleteApplicationById(applicationId)
                                        }

                                        DocumentChange.Type.ADDED,
                                        DocumentChange.Type.MODIFIED -> {
                                            applicationDao.cacheApplications(
                                                application.toDutyApplicationEntity(applicationId)
                                            )
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("DutyRepo", "Failed to sync application changes", e)
                            }
                        }
                    }
                }
            awaitClose { applicationListenerRegistration.remove() }
        }

    private suspend fun revertLocalApplication(
        application: DutyApplication,
        applicationId: String
    ) {
        try {
            // 1. Remove the application record from Room
            applicationDao.deleteApplicationById(applicationId)

            // 2. Rollback (decrement) the reserved count in Room
            dutyDao.getDutyById(application.dutyId)?.let { cachedDuty ->
                val updatedCount = (cachedDuty.btsReservedCount - 1).coerceAtLeast(0)
                val updatedDuty = cachedDuty.copy(btsReservedCount = updatedCount)
                dutyDao.cacheDuties(listOf(updatedDuty))
            }

            Log.d(
                "RepoDebug",
                "Successfully reverted local application for duty: ${application.dutyId}"
            )
        } catch (e: Exception) {
            Log.e("RepoDebug", "Failed to revert local application: ${e.message}")
        }
    }

}
