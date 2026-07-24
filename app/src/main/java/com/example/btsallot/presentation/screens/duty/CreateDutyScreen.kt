package com.example.btsallot.presentation.screens.duty

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.data.model.Duty
import com.example.btsallot.data.model.DutyForm
import com.example.btsallot.data.model.DutyTemplate
import com.example.btsallot.domain.utils.DutyData
import com.example.btsallot.domain.utils.fromMinutes
import com.example.btsallot.domain.utils.toMinutes
import com.example.btsallot.presentation.designsystem.buttons.PrimaryButton
import com.example.btsallot.presentation.designsystem.selections.OptionsSelectionSheet
import com.example.btsallot.presentation.designsystem.selections.TimeSelectionSheet
import com.example.btsallot.presentation.designsystem.textfields.DropdownTextField
import com.example.btsallot.presentation.designsystem.textfields.InputAlertDialog
import com.example.btsallot.presentation.designsystem.textfields.InputTextField
import com.example.btsallot.presentation.designsystem.textfields.StepperTextField
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.BackgroundLight
import com.example.btsallot.presentation.theme.TextPrimary

@Composable
fun CreateDutyScreen(
    dateFromCalendar: String?,
    onBackClick: () -> Unit = {},
    onSaveClick: (CreateDutyResult) -> Unit = {},
    isTemplate: Boolean
) {
    var activePicker by rememberSaveable{ mutableStateOf<PickerTarget?>(null) }
    var day by remember { mutableStateOf("") }
    var dutyName by remember { mutableStateOf("") }
    var customDutyName by remember { mutableStateOf("") }
    var startMinutes by remember { mutableStateOf<Int?>(null) }
    var endMinutes by remember { mutableStateOf<Int?>(null) }
    var location by remember { mutableStateOf("") }
    var customLocation by remember { mutableStateOf("") }
    var btsRequired by remember { mutableIntStateOf(2) }
    var notes by remember { mutableStateOf("") }

    val isDateOrDayValid = if (isTemplate) {
        day.isNotBlank()
    } else {
        !dateFromCalendar.isNullOrBlank()
    }

    val isFormValid =
        isDateOrDayValid &&
                dutyName.isNotBlank() &&
                startMinutes != null &&
                endMinutes != null &&
                location.isNotBlank() &&
                btsRequired >= 1

    when(activePicker){
        PickerTarget.DATE ->{
            OptionsSelectionSheet(
                title = "Select Day",
                options = DutyData.days,
                selectedOption = day,
                onOptionSelected = {
                    day = it
                    activePicker = null},
                onDismiss = {activePicker = null}
            )
        }
        PickerTarget.DUTY_TYPE -> {
            OptionsSelectionSheet(
                title = "Select Duty Type",
                options = DutyData.dutyTypes,
                selectedOption = dutyName,
                onOptionSelected = {option->
                    if (option == "Others"){
                        activePicker = PickerTarget.CUSTOM_DUTY_TYPE
                    }
                    else{
                        dutyName = option
                        activePicker = null
                    }
                    },
                onDismiss = {activePicker = null}
            ) 
        }
        PickerTarget.CUSTOM_DUTY_TYPE -> {
            InputAlertDialog(
                title = "Name of the Duty:",
                value = customDutyName,
                onValueChange = { customDutyName = it },
                onConfirm = {
                    dutyName = customDutyName.trim()
                    activePicker = null
                },
                onDismiss = {
                    activePicker = null
                },
                label = "Duty Name"
            )

        }
        PickerTarget.START_TIME -> {
            TimeSelectionSheet(
                onDismiss = {activePicker = null},

                onTimeSelected = {hour, mins ->
                    startMinutes = toMinutes(hour, mins)
                }
            )
        }
        PickerTarget.END_TIME -> {
            TimeSelectionSheet(
                onDismiss = {activePicker = null},
                onTimeSelected = {hour, mins ->
                    endMinutes = toMinutes(hour, mins)
                }
            )
        }
        PickerTarget.LOCATION -> {
            OptionsSelectionSheet(
                title = "Select Location",
                options = DutyData.locations,
                selectedOption = location,
                onOptionSelected = {option->
                    if (option == "Others"){
                        activePicker = PickerTarget.CUSTOM_LOCATION
                    }
                    else{
                        location = option
                        activePicker = null
                    }
                    },
                onDismiss = {activePicker = null}
            )
        }
        PickerTarget.CUSTOM_LOCATION -> {
            InputAlertDialog(
                title = "Update Location:",
                value = customLocation,
                onValueChange = { customLocation = it },
                onConfirm = {
                    location = customLocation.trim()
                    activePicker = null
                },
                onDismiss = {
                    activePicker = null
                },
                label = "Location"
            )
        }
        else -> Unit
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                         )
                }
                Text(text = "Create Duty", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            val dayLabel = if (isTemplate) "Day" else "Date"
            val selectedDate = if (isTemplate)day else dateFromCalendar.orEmpty()

            //Day-Date field
            DropdownTextField(
                label = dayLabel,
                value = selectedDate,
                enabled = isTemplate,
                onClick = {
                    if (isTemplate){
                        activePicker = PickerTarget.DATE
                    }
                    else {activePicker = null}
                },
                isCalendar = true
            )
            Spacer(modifier = Modifier.height(18.dp))

            //Duty field
            DropdownTextField(
                label = "Duty Type",
                value = dutyName,
                onClick = {
                    activePicker = PickerTarget.DUTY_TYPE
                }
            )
            Spacer(modifier = Modifier.height(18.dp))

            //Time fields in row
            Row(modifier = Modifier.fillMaxWidth()) {
                DropdownTextField(
                    label = "Start Time",
                    value = startMinutes?.let { fromMinutes(it) }.orEmpty(),
                    onClick = {
                        activePicker = PickerTarget.START_TIME
                    },
                    trailingIcon = Icons.Default.AccessTime,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(14.dp))
                DropdownTextField(
                    label = "End Time",
                    value = endMinutes?.let { fromMinutes(it)}.orEmpty(),
                    onClick = {
                        activePicker = PickerTarget.END_TIME
                    },
                    trailingIcon = Icons.Default.AccessTime,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))

           // Location field
            DropdownTextField(
                label = "Location",
                value = location,
                onClick = {
                    activePicker = PickerTarget.LOCATION
                },
            )

            Spacer(modifier = Modifier.height(18.dp))

            StepperTextField(
                label = "# BTS",
                value = btsRequired,
                onValueChange = {btsRequired = it},
                modifier = Modifier.fillMaxWidth(0.4f),
                minValue = 1,
            )

            Spacer(modifier = Modifier.height(18.dp))

            //Notes field
            InputTextField(
                label = "Notes (Optional)",
                value = notes,
                onValueChange = {notes = it},
                modifier = Modifier.fillMaxWidth().height(110.dp)

            )
            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButton(
                onClick = {
                    val form = DutyForm(
                        meetingName = dutyName,
                        startMinutes = startMinutes!!,
                        endMinutes = endMinutes!!,
                        btsRequired = btsRequired,
                        location = location,
                        notes = notes.takeIf { it.isNotBlank() }
                        //if user leaves notes empty -> notes = null (as per data class)
                        //user types notes -> notes = "text"
                    )
                        if (isTemplate){
                            onSaveClick(CreateDutyResult.Template(
                                template = DutyTemplate(
                                    dayOfWeek =day,
                                    duty = form
                                )
                            ))
                        }
                    else{
                        onSaveClick(
                            CreateDutyResult.Manual(
                                Duty(
                                    date = dateFromCalendar!!,
                                    duty = form

                                )
                            )
                        )
                        }
                },
                enabled = isFormValid,
                text = "Save",
               // modifier = Modifier.width(100.dp)
            )
        }
    }
}





@Preview(showBackground = true)
@Composable
private fun CreateDutyScreenPreview() {
    BTSAllotTheme { CreateDutyScreen(
        isTemplate = false,
        dateFromCalendar = "") }
}


private enum class PickerTarget{
    DATE,
    DUTY_TYPE,
    CUSTOM_DUTY_TYPE,
    START_TIME,
    END_TIME,
    LOCATION,
    CUSTOM_LOCATION

}


sealed interface CreateDutyResult{
    data class Manual(val duty: Duty): CreateDutyResult
    data class Template(val template: DutyTemplate): CreateDutyResult
}


//update the start end time data type in data class and here, then create templates
