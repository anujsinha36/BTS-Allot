package com.example.btsallot.presentation.designsystem.selections

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.StatusFull
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectionSheet(
    onDismiss :  () -> Unit,
    onTimeSelected: (hour: Int, mins: Int) -> Unit
){
    val timePickerState = rememberTimePickerState()
     TimePickerDialog(
        onDismissRequest = onDismiss,
         shape = RoundedCornerShape(24.dp),
         containerColor = MaterialTheme.colorScheme.primaryContainer,
        confirmButton = {
            TextButton(onClick = {
               onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismiss()
            }) {
                Text("Okay",color = MaterialTheme.colorScheme.primary,)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text("Cancel", color = MaterialTheme.colorScheme.primary,)
            }
        },
        title = {
            Text("Select Time",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                textAlign = TextAlign.Center
                )
                },

        ){
        TimePicker(
            state = timePickerState,
            //these are just for Timer colors
            colors = TimePickerDefaults.colors(
                selectorColor = MaterialTheme.colorScheme.primary,
                clockDialColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,

                periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                periodSelectorSelectedContentColor = SurfaceWhite,
                periodSelectorUnselectedContainerColor = SurfaceWhite,
                periodSelectorUnselectedContentColor = TextSecondary,
                periodSelectorBorderColor = StatusFull,
            )
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewSelectionSheet(){
    BTSAllotTheme {
   //     SelectionSheet {
            TimePicker(state = rememberTimePickerState())

      //  }
    }
}