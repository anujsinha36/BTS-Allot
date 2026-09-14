package com.example.btsallot.presentation.screens.duty

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.btsallot.presentation.viewmodels.DutyViewModel
import java.time.LocalDate

@Composable
fun CreateDutyScreenContainer(
    dateFromCalendar: LocalDate?,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    isTemplate: Boolean
){
    val viewModel: DutyViewModel = hiltViewModel()

    CreateDutyScreen(
        dateFromCalendar = dateFromCalendar,
        onBackClick = onBackClick,
        onSaveClick = { result->
            when(result){
                is CreateDutyResult.Manual -> {
                    viewModel.createDuty(result.duty)
                }
                is CreateDutyResult.Template -> {
                    viewModel.createTemplate(result.template)
                }
            }
            onSaveClick () // this is called to continue with another action within Navgraph after viewmodel work
                      },
        isTemplate = isTemplate
    )
}