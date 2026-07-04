package com.example.btsallot.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btsallot.data.model.Duty
import com.example.btsallot.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
): ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signInWithGoogle(){
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val tokenResult = repository.getGoogleIdToken()
            if (tokenResult.isFailure){
                val exception = tokenResult.exceptionOrNull()
                _authState.value = AuthState.Error(
                    exception?.message ?: "Could not start Google sign-in. Please make sure a Google account is available on this device and try again."
                )
                return@launch
            }
            val idToken = tokenResult.getOrThrow()
            val signInResult = repository.signInWithFirebase(idToken)

            if (signInResult.isFailure){
                _authState.value = AuthState.Error("Google Sign-in failed.")
            return@launch
            }

            val user = signInResult.getOrThrow()
            val userDocResult = repository.ensureUserDocumentExists(user)
            if (userDocResult.isFailure) {
                _authState.value = AuthState.Error("Failed to save user details")
                return@launch
            }

            _authState.value = AuthState.Success(user)

        }
    }

    fun signOutWithGoogle(){
        repository.signOut()
        _authState.value = AuthState.Idle
    }

    fun createDuty(duty: Duty){
        viewModelScope.launch {
            repository.createDuty(duty)
        }

    }

}

sealed class AuthState(){
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser): AuthState()
    data class Error(val message: String): AuthState()
}