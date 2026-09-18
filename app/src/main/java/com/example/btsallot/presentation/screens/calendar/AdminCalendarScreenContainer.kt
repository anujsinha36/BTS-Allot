package com.example.btsallot.presentation.screens.calendar

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.btsallot.presentation.viewmodels.DutyViewModel
import java.time.LocalDate

@Composable
fun AdminCalendarScreenContainer(
    onDateClicked: (LocalDate) -> Unit = {},
    onCreateTemplate: ()-> Unit = {},
    onBtsScreen: () -> Unit = {}
){
    val viewModel: DutyViewModel = hiltViewModel()

    AdminCalenderScreen(
        onDateClicked = onDateClicked,
        onCreateTemplate = onCreateTemplate,
        onBtsScreen = onBtsScreen,
        onSync = {
            viewModel.syncData()
        }
    )
}