package com.example.btsallot.data.repository


import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.btsallot.R
import com.example.btsallot.data.mappers.toDomainUser
import com.example.btsallot.data.model.FirestoreUser
import com.example.btsallot.domain.model.User
import com.example.btsallot.domain.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.collections.hashMapOf

class AuthRepositoryImpl(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val firestoreDB : FirebaseFirestore
): AuthRepository {
    //private val appContext = context.applicationContext
    private val credentialManager = CredentialManager.create(context)

    // Job 1: Get ID token from Google
    override suspend fun getGoogleIdToken(activityContext: Context): Result<String>{

        return try {

            // Tell Credential Manager we want a Google sign in
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()

            //Build request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // This line actually shows the Google account picker to the user
            // and suspends (waits) until the user picks an account
            val result = credentialManager.getCredential(activityContext,request)
            val credential = result.credential

            // Check we got the right type of credential
            if(credential is CustomCredential &&
                credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){

                // Extract the ID token from it
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(googleIdTokenCredential.idToken)
            }
            else{
                Result.failure(Exception("Wrong credential type"))
            }
        }
        // User canceled, or no accounts found
        catch (e: GetCredentialException){
            // can also have generic exception
            Result.failure(e)
        }
    }

    // Job 2: Give the ID token to Firebase
    override suspend fun signInWithFirebase(idToken: String): Result<User>{

        return try {
            // Wrap the token in a format Firebase understands
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

            // Actually sign in — .await() turns the callback into a suspend function
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user ?: throw Exception("Sign in failed")

            ensureUserDocumentExists(firebaseUser)
            getCurrentUserDetails()
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUserDetails(): Result<User> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No user logged in"))
        return try {
            val snapshot = firestoreDB.collection("users")
                .document(uid).get().await()
            val firestoreUser = snapshot.toObject(FirestoreUser::class.java)
                ?: throw Exception("Failed to map Firestore document")
            Result.success(firestoreUser.toDomainUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //see if we can create a User data class as well

    override fun signOut(){
        auth.signOut()
    }

    private suspend fun ensureUserDocumentExists(user: FirebaseUser): Result<Unit>{

        return try {
            val userDoc = firestoreDB.collection("users")
                .document(user.uid)

            val snapshot = userDoc.get().await()

            if (!snapshot.exists()){
                val firestoreUser = FirestoreUser(
                    uid = user.uid,
                    name = user.displayName ?: user.email?.substringBefore("@") ?: "User",
                    email = user.email ?: "",
                    role = "VOLUNTEER",
                    photoUrl = user.photoUrl?.toString()
                )
                userDoc.set(firestoreUser).await()
            }
            Result.success(Unit)
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }
}
