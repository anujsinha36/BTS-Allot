package com.example.btsallot.presentation.designsystem.selections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.btsallot.presentation.theme.BTSAllotTheme

@Composable
fun OptionsSelectionSheet(
    title: String,
    onDismiss: () -> Unit

){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      //  OptionsSelectionSheetContent()
    }
}


@Composable
fun OptionsSelectionSheetContent(
    title: String,
    onDismiss: () -> Unit,
    options: List<String>,
    onOptionsSelected: (String) -> Unit,
    selectedOption: String? = null
){
    Surface(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 10.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = {onDismiss()}) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.padding(horizontal = 22.dp))
            {items(options){ option->
                SelectionItem(
                    text = option,
                    onCLick = {onOptionsSelected(option)},
                    isSelected = option == selectedOption
                )

            }

            }

        }
    }


}



@Preview(showBackground = true)
@Composable
fun PreviewSelectionSheetContent(){
    BTSAllotTheme {
        var selectedOption by remember { mutableStateOf<String?>(null) }
        OptionsSelectionSheetContent(
            title = "Select Type of Duty",
            onDismiss = {},
            options = listOf("Cleaning Duty", "Morning Roster", "Adhoc Meeting"),
            onOptionsSelected = {selectedOption = it},
            selectedOption = selectedOption
        )
    }
}