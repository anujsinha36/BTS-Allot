package com.example.btsallot.presentation.screens.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.presentation.designsystem.states.ErrorState
import com.example.btsallot.presentation.viewmodels.DutyViewModel
import java.time.LocalDate

@Composable
fun AdminCalendarScreenContainer(
    onDateClicked: (LocalDate) -> Unit = {},
    onCreateTemplate: () -> Unit = {},
    onBtsScreen: () -> Unit = {},
    onDutyClick: (String) -> Unit = {}
) {
    val dutyViewModel: DutyViewModel = hiltViewModel()
    val uiState by dutyViewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        dutyViewModel.startRealTimeSync()
    }
    when {
        //            uiState.isLoading && uiState.duties.isEmpty() ->{
//                LoadingState(message = "Getting Calendar...")
//            }
        uiState.errorMessage != null && uiState.duties.isEmpty() -> {
            ErrorState(
                message = uiState.errorMessage,
                onRetry = { dutyViewModel.retry() }
            )
        }

        else -> {
            AdminCalenderScreen(
                duties = uiState.duties,
                onDateClicked = onDateClicked,
                onCreateTemplate = onCreateTemplate,
                onBtsScreen = onBtsScreen,
                onDutyClick = onDutyClick
            )
        }
    }


}