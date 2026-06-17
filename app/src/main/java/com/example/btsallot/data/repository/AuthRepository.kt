package com.example.btsallot.data.repository


import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.btsallot.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import kotlin.collections.hashMapOf
import kotlin.math.sqrt

class AuthRepository(private val context: Context) {
    private val auth = Firebase.auth
    private val firestoreDB = Firebase.firestore
    private val credentialManager = CredentialManager.create(context)

    // Job 1: Get ID token from Google
    suspend fun getGoogleIdToken(): Result<String>{

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
            val result = credentialManager.getCredential(context,request)
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
    suspend fun signInWithFirebase(idToken: String): Result<FirebaseUser>{

        return try {
            // Wrap the token in a format Firebase understands
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

            // Actually sign in — .await() turns the callback into a suspend function
            val authResult = auth.signInWithCredential(firebaseCredential).await()

            val user = authResult.user
            if (user != null){
                Result.success(user)
            }
            else Result.failure(Exception("Sign in failed"))
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun ensureUserDocumentExists(user: FirebaseUser): Result<Unit> {
       return try {
           val userDoc = firestoreDB.collection("users")
               .document(user.uid)

           val snapshot = userDoc.get().await()

           if (!snapshot.exists()){
               val userData = hashMapOf(
                   "uid" to user.uid,
                   "name" to user.displayName,
                   "email" to user.email,
                   "role" to "VOLUNTEER",
                   "photo" to user.photoUrl?.toString()
               )
               userDoc.set(userData).await()
           }
           Result.success(Unit)
       }
       catch (e: Exception){
           Result.failure(e)
       }
    }

    fun signOut(){
        auth.signOut()
    }
}
