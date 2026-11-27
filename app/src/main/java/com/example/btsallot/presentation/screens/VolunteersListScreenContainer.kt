package com.example.btsallot.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.presentation.designsystem.states.ErrorState
import com.example.btsallot.presentation.designsystem.states.LoadingState
import com.example.btsallot.presentation.viewmodels.VolunteersViewModel

@Composable
fun VolunteersListScreenContainer(
    viewModel: VolunteersViewModel = hiltViewModel(),
    onVolunteerClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            LoadingState(message = "Loading Volunteers...")
        }
        uiState.errorMessage != null && uiState.volunteers.isEmpty() -> {
            ErrorState(
                message = uiState.errorMessage ?: "An error occurred",
                onRetry = { viewModel.startRealTimeSync() }
            )
        }
        else -> {
            VolunteersListScreen(
                uiState = uiState,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onFilterSelected = viewModel::onFilterSelected,
                onMonthSelected = viewModel::onMonthSelected,
                onVolunteerClick = onVolunteerClick
            )
        }
    }
}
