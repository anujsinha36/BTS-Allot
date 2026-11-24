package com.example.btsallot.presentation.screens.duty

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.btsallot.presentation.designsystem.states.ErrorState
import com.example.btsallot.presentation.designsystem.states.LoadingState
import com.example.btsallot.presentation.screens.DutyDetailsScreen
import com.example.btsallot.presentation.viewmodels.DutyDetailsViewModel

@Composable
fun DutyDetailsScreenContainer(
    dutyId: String,
    isAdmin: Boolean
){
    val viewModel: DutyDetailsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(dutyId) {
        Log.d("APP_DEBUG", "Details opened dutyId='$dutyId'")
        viewModel.loadDuty(dutyId)
        viewModel.realTimeApplicationSync()
    }
    val duty = state.duty

    when{
        state.loading->{
            LoadingState()
        }

        state.errorMessage != null -> {
            ErrorState(
                message = state.errorMessage
            )
        }

        duty != null -> {
            DutyDetailsScreen(
                duty = duty,
                isAdmin = isAdmin,
                applications = state.applications
                )
        }

        else ->{
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Duty not found")
            }
        }
    }

}