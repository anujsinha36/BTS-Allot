package com.example.btsallot.presentation.screens.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.toUpperCase
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.presentation.designsystem.scaffold.BottomNavItem
import com.example.btsallot.presentation.viewmodels.AuthState
import com.example.btsallot.presentation.viewmodels.AuthViewModel
import com.example.btsallot.presentation.viewmodels.DutyViewModel
import java.time.LocalDate

@Composable
fun BTSCalendarScreenContainer(
    onDateClicked: (LocalDate) -> Unit = {},
    authViewModel: AuthViewModel,
    onNavigation: (BottomNavItem) -> Unit = {}
){
    val dutyViewModel: DutyViewModel = hiltViewModel()
    val duties by dutyViewModel.duties.collectAsStateWithLifecycle()
    val appliedDutyIds by dutyViewModel.dutyID.collectAsStateWithLifecycle()

    val authState by authViewModel.authState.collectAsStateWithLifecycle()



    // Render cached data immediately, then refresh it when this calendar opens.
    LaunchedEffect(authState) {
        (authState as? AuthState.Success)?.let {
            dutyViewModel.getAppliedDutyId(it.user.uid)
        }
    }

    (authState as? AuthState.Success)?.let { it->
        val currentUser = it.user
        val firstName = currentUser.name.substringBefore(" ")
            .lowercase().replaceFirstChar { it.uppercase() }

        BTSCalenderScreen(
            duties = duties,
            onDateClicked = onDateClicked,
            user = firstName,
            onDutyApplyClicked = {duty ->
                val application = DutyApplication(
                    dutyId = duty.id,
                    userId = currentUser.uid,
                    userName = currentUser.name
                )
                dutyViewModel.createApplication(application)

            }, appliedDutyId = appliedDutyIds,
            onNavigation = onNavigation
        )
    }



}
