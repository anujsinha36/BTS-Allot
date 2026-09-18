package com.example.btsallot.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btsallot.data.repository.AuthRepositoryImpl
import com.example.btsallot.domain.model.User
import com.example.btsallot.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        loadUser()
    }

    fun signInWithGoogle(activityContext: Context){
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val tokenResult = repository.getGoogleIdToken(activityContext)
            if (tokenResult.isFailure){
                val exception = tokenResult.exceptionOrNull()
                _authState.value = AuthState.Error(
                    exception?.message ?: "Could not start Google sign-in. Please make sure a Google account is available on this device and try again."
                )
                return@launch
            }
            val idToken = tokenResult.getOrThrow()
            val signInResult = repository.signInWithFirebase(idToken)

            if(signInResult.isSuccess){
                _authState.value = AuthState.Success(signInResult.getOrThrow())
            }
            else{
                _authState.value = AuthState.Error("Login failed: ${signInResult.exceptionOrNull()?.message}")
            }
        }
    }

    fun loadUser(){
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.getCurrentUserDetails().onSuccess { currentUser->
                _authState.value = AuthState.Success(currentUser)
            }.onFailure {
                _authState.value = AuthState.Error("Session Expired")
                //check for more error messages and ways to identify
            }
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
    data class Success(val user: User): AuthState()
    data class Error(val message: String): AuthState()
}