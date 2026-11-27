package com.example.btsallot.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.presentation.designsystem.states.ErrorState
import com.example.btsallot.presentation.designsystem.states.LoadingState
import com.example.btsallot.presentation.viewmodels.DashboardViewModel

@Composable
fun DashboardScreenContainer(
    viewModel: DashboardViewModel = hiltViewModel(),
    onViewAllClick: () -> Unit = {},
    onDutyClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            LoadingState(message = "Loading Dashboard...")
        }
        uiState.errorMessage != null && uiState.upcomingDuties.isEmpty() -> {
            ErrorState(
                message = uiState.errorMessage ?: "An error occurred",
                onRetry = { viewModel.startRealTimeSync() }
            )
        }
        else -> {
            DashboardScreen(
                uiState = uiState,
                onMonthSelected = viewModel::onMonthSelected,
                onViewAllClick = onViewAllClick,
                onDutyClick = onDutyClick
            )
        }
    }
}
