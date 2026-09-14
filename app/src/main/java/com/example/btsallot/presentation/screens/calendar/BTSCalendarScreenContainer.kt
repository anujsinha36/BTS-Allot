package com.example.btsallot.presentation.screens.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.presentation.viewmodels.DutyViewModel
import java.time.LocalDate

@Composable
fun BTSCalendarScreenContainer(
    onDateClicked: (LocalDate) -> Unit = {},

){
    val viewModel: DutyViewModel = hiltViewModel()
    val duties by viewModel.duties.collectAsStateWithLifecycle()

    // Render cached data immediately, then refresh it when this calendar opens.
    LaunchedEffect(Unit) {
        viewModel.syncData()
    }

    BTSCalenderScreen(
        duties = duties,
        onDateClicked = onDateClicked
    )
}
