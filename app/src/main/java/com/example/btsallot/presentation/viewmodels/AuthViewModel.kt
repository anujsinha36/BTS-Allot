package com.example.btsallot.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                _authState.value = AuthState.Error(tokenResult.exceptionOrNull()?.message ?:"Failed to receive ID token")
                return@launch
            }
            val idToken = tokenResult.getOrThrow()
            val signInResult = repository.signInWithFirebase(idToken)

            _authState.value = if (signInResult.isSuccess){
                AuthState.Success(signInResult.getOrThrow())
            }
            else AuthState.Error("Google Sign-in failed.")

        }
    }

    fun signOutWithGoogle(){
        repository.signOut()
        _authState.value = AuthState.Idle
    }

}

sealed class AuthState(){
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser): AuthState()
    data class Error(val message: String): AuthState()
}