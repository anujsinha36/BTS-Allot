package com.example.btsallot.presentation.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.presentation.designsystem.scaffold.BottomNavItem
import com.example.btsallot.presentation.designsystem.states.ErrorState
import com.example.btsallot.presentation.designsystem.states.LoadingState
import com.example.btsallot.presentation.viewmodels.AuthState
import com.example.btsallot.presentation.viewmodels.AuthViewModel

@Composable
fun ProfileScreenContainer(
    authViewModel: AuthViewModel,
    onNavigation: (BottomNavItem) -> Unit,
    onLogout: () -> Unit
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (val state = authState) {
        is AuthState.Success -> {
            ProfileScreen(
                profile = state.user,
                onLogout = onLogout,
                onNavigation = onNavigation
            )
        }
        AuthState.Loading,
        AuthState.Idle -> {
            LoadingState(message = "Loading profile...")
        }
        is AuthState.Error -> {
            ErrorState(
                message = state.message,
                onRetry = { authViewModel.loadUser() }
            )
        }
    }
}