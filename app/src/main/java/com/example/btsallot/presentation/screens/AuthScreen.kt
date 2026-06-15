package com.example.btsallot.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.presentation.viewmodels.AuthState
import com.example.btsallot.presentation.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onSignInSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    // When state becomes Success, navigate away
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSignInSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (authState) {

            is AuthState.Idle -> {
                Button(onClick = { viewModel.signInWithGoogle() }) {
                    Text("Sign in with Google")
                }
            }

            is AuthState.Loading -> {
                CircularProgressIndicator()
            }

            is AuthState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.signInWithGoogle() }) {
                        Text("Try again")
                    }
                }
            }

            is AuthState.Success -> {
                // LaunchedEffect handles navigation, nothing to show here
            }
        }
    }
}