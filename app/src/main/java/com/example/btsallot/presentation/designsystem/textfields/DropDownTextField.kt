package com.example.btsallot.presentation.designsystem.textfields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.presentation.theme.BTSAllotTheme
@Composable
fun DropdownTextField(
    label: String,
    value: String,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    isCalendar: Boolean = false,
    enabled: Boolean = true
) {
    Box(modifier = modifier.fillMaxWidth()){
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onTertiaryFixed)
                    },
            trailingIcon = {
                val icon = trailingIcon ?: if(isCalendar) Icons.Default.EditCalendar
                else Icons.Default.KeyboardArrowDown
                    Icon(
                        imageVector = icon,
                        contentDescription = "Dropdown",
                    //    tint = Color(0xFF757575)
                    )
            },
            readOnly = true,
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.4f),
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(14.dp)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewDropDownTextField(){
    BTSAllotTheme {
        DropdownTextField(
            label = "Brand",
            value = "",
            onClick = {},
            modifier = Modifier.padding(16.dp)

        )
    }
}