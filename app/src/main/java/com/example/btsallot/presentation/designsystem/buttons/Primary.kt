package com.example.btsallot.presentation.designsystem.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.SurfaceWhite

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean
){
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        enabled = enabled,
       colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = text,
           // style = MaterialTheme.typography.titleSmall,
          //  color = SurfaceWhite
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    BTSAllotTheme {
        PrimaryButton(
            onClick = {},
            text = "Primary Button",
            enabled = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonDisabledPreview() {
    BTSAllotTheme {
        PrimaryButton(
            onClick = {},
            text = "Disabled Button",
            enabled = false
        )
    }
}

