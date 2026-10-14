package com.example.btsallot.presentation.screens.duty

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.presentation.designsystem.scaffold.BottomNavItem
import com.example.btsallot.presentation.designsystem.states.ErrorState
import com.example.btsallot.presentation.viewmodels.AuthState
import com.example.btsallot.presentation.viewmodels.AuthViewModel
import com.example.btsallot.presentation.viewmodels.DutyViewModel

@Composable
fun MyDutiesContainer(
    authViewModel: AuthViewModel,
    onNavigation: (BottomNavItem) -> Unit = {}
){
    val dutyViewModel: DutyViewModel = hiltViewModel()
    val uiState by dutyViewModel.uiState.collectAsStateWithLifecycle()

    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        (authState as? AuthState.Success)?.let {
            dutyViewModel.observeMyDuties(userId = it.user.uid)
        }
    }
    when{
//        uiState.isLoading && uiState.duties.isEmpty() ->{
//                LoadingState(message = "Getting Calendar...")
//            }
            uiState.errorMessage != null && uiState.duties.isEmpty() -> {
                ErrorState(
                    message = uiState.errorMessage,
                    onRetry = { dutyViewModel.retry() }
                )
            }
            else ->{
                MyDutiesScreen(
                    upcomingDuties = uiState.upcomingAppliedDuties,
                    pastDuties = uiState.pastAppliedDuties,
                    onNavigation = onNavigation,
                    onDeleteApplication = {}
                )
            }
    }
}