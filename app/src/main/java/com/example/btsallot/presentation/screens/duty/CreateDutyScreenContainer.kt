package com.example.btsallot.presentation.screens.duty

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.btsallot.data.repository.AuthRepository
import com.example.btsallot.presentation.screens.CreateDutyResult
import com.example.btsallot.presentation.screens.CreateDutyScreen
import com.example.btsallot.presentation.viewmodels.AuthViewModel

@Composable
fun CreateDutyScreenContainer(
    dateFromCalendar: String?,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    isTemplate: Boolean
){
    val context = LocalContext.current.applicationContext
    val viewModel: AuthViewModel = viewModel(
        factory = object :  ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(
                    repository = AuthRepository(context)
                ) as T
            }
        }
    )

    CreateDutyScreen(
        dateFromCalendar = dateFromCalendar,
        onBackClick = onBackClick,
        onSaveClick = { result->
            when(result){
                is CreateDutyResult.Manual -> {
                    viewModel.createDuty(result.duty)
                }
                is CreateDutyResult.Template -> {
                    viewModel.createTemplates(result.template)
                }
            }
            onSaveClick ()
                      },
        isTemplate = isTemplate
    )
}