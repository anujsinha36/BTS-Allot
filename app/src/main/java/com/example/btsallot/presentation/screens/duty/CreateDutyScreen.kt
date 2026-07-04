package com.example.btsallot.presentation.screens.duty


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.btsallot.data.model.Duty
import com.example.btsallot.data.repository.AuthRepository
import com.example.btsallot.presentation.viewmodels.AuthViewModel


@Composable
fun DialogSheet(
    date: String,
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {}
){
    CreateDutySheet(date, onSave, onCancel)
}


@Composable
fun CreateDutySheet(
    date1: String,
    onSave: () -> Unit,
    onCancel: () -> Unit

){
    // var date = remember { mutableStateOf("") }
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
    val sokahanCount = remember { mutableStateOf("") }
    val isFormValid = date1.isNotBlank() && sokahanCount.value.isNotBlank()


    Surface() { Column(){
        InputTextField(
            value = date1,
            onValueChange = {},
            label = "Date",
            readOnly = true
        )
        InputTextField(
            label = "Number of Sokahan",
            value = sokahanCount.value,
            readOnly = false,
            onValueChange = { newValue->
                if (newValue.isDigitsOnly()){
                    sokahanCount.value = newValue
                }
                 },
            keyType = KeyboardType.Number
        )
        Row{
            Button(enabled = isFormValid,
                onClick = {
                val duty = Duty(
                    date = date1,
                    requiredVolunteers = sokahanCount.value
                )
                viewModel.createDuty(duty)
                onSave()
            }) { Text("Save")}
            Button(onClick = {
                onCancel()
            }) { Text("Cancel")}
        }


    }
    }


}


@Composable
fun InputTextField(
    label : String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean,
    keyType: KeyboardType = KeyboardType.Text
){
    Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(keyboardType = keyType),
            label = {
                Text(
                    text = label
                )
            }
        )
    }


}


@Preview(showBackground = true)
@Composable
fun PreviewDialogSheet(){
    DialogSheet(
date = ""
    )
}

