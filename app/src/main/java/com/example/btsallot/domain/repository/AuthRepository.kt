package com.example.btsallot.domain.repository

import android.content.Context
import com.example.btsallot.domain.model.User


interface AuthRepository {

    suspend fun getGoogleIdToken(activityContext: Context): Result<String>
    suspend fun signInWithFirebase(idToken: String): Result<User>
    suspend fun getCurrentUserDetails(): Result<User>
    fun signOut()
}