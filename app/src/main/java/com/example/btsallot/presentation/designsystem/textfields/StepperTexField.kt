package com.example.btsallot.presentation.designsystem.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.presentation.theme.BTSAllotTheme

@Composable
fun StepperTextField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int? = null
) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        leadingIcon = {
            IconButton(
                onClick = {
                    if (value > minValue) {
                        onValueChange(value - 1)
                    }
                }
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (maxValue == null || value < maxValue) {
                        onValueChange(value + 1)
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center
        ),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.4f),
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.4f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewStepperFunction(){
    BTSAllotTheme { 
        StepperTextField(
            label = "BTS Count",
            value = 2,
            onValueChange = {},
            minValue = 1,
        )
    }
}