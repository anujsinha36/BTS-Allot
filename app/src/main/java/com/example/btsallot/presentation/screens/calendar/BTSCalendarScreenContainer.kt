package com.example.btsallot.presentation.screens.calendar

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.btsallot.presentation.viewmodels.DutyViewModel
import java.time.LocalDate

@Composable
fun BTSCalendarScreenContainer(
    onDateClicked: (LocalDate) -> Unit = {},

){
    val viewModel: DutyViewModel = hiltViewModel()
    val duties = viewModel.duties.value

    BTSCalenderScreen(
        duties = duties,
        onDateClicked = onDateClicked
    )
}