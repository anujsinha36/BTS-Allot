package com.example.btsallot.presentation.screens.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.toUpperCase
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.presentation.designsystem.scaffold.AppScaffold
import com.example.btsallot.presentation.designsystem.scaffold.BottomNavItem
import com.example.btsallot.presentation.designsystem.states.ErrorState
import com.example.btsallot.presentation.designsystem.states.LoadingState
import com.example.btsallot.presentation.viewmodels.AuthState
import com.example.btsallot.presentation.viewmodels.AuthViewModel
import com.example.btsallot.presentation.viewmodels.DutyViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun BTSCalendarScreenContainer(
    onDateClicked: (LocalDate) -> Unit = {},
    authViewModel: AuthViewModel,
    onNavigation: (BottomNavItem) -> Unit = {}
){
    val dutyViewModel: DutyViewModel = hiltViewModel()
    val uiState by dutyViewModel.uiState.collectAsStateWithLifecycle()

    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    // Render cached data immediately, then refresh it when this calendar opens.
    LaunchedEffect(authState) {
        (authState as? AuthState.Success)?.let {
            dutyViewModel.getAppliedDutyId(it.user.uid)
        }
    }

    LaunchedEffect(Unit) {
        dutyViewModel.startRealTimeSync()
    }

    (authState as? AuthState.Success)?.let { it->
        val currentUser = it.user
        val firstName = currentUser.name.substringBefore(" ")
            .lowercase().replaceFirstChar { it.uppercase() }
        when{
//            uiState.isLoading && uiState.duties.isEmpty() ->{
//                LoadingState(message = "Getting Calendar...")
//            }
            uiState.errorMessage != null && uiState.duties.isEmpty() -> {
                ErrorState(
                    message = uiState.errorMessage,
                    onRetry = { dutyViewModel.retry() }
                )
            }
            else ->{
                BTSCalenderScreen(
                    duties = uiState.duties,
                    onDateClicked = onDateClicked,
                    user = firstName,
                    onDutyApplyClicked = {duty ->
                        val application = DutyApplication(
                            dutyId = duty.id,
                            userId = currentUser.uid,
                            userName = currentUser.name
                        )
                        dutyViewModel.createApplication(application)

                    }, appliedDutyId = uiState.appliedDutyIds,
                    onNavigation = onNavigation
                )
            }
            }
        }
}

//design profile screen
//design remaining screens for admin and how calendar and duty should be available
//have admin/BTS separate flow logic