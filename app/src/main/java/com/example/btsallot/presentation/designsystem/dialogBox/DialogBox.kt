package com.example.btsallot.presentation.designsystem.dialogBox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.LogoutRed
import com.example.btsallot.presentation.theme.StatusAssigned
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextSecondary
import com.example.btsallot.presentation.theme.dimensions

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String = "Delete Duty",
    message: String = "Are you sure you want to delete this duty?",
    cancelText: String = "Cancel",
    confirmText: String = "Delete"
) {
    ConfirmationDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = title,
        message = message,
        cancelText = cancelText,
        confirmText = confirmText,
        icon = Icons.Filled.Delete,
        accentColor = LogoutRed,
        showAsDialog = true
    )
}

@Composable
fun CreateApplyConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String = "Confirm",
    message: String = "Are you sure you want to continue?",
    cancelText: String = "Cancel",
    confirmText: String = "Confirm"
) {
    ConfirmationDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = title,
        message = message,
        cancelText = cancelText,
        confirmText = confirmText,
        icon = Icons.Filled.CheckCircle,
        accentColor = StatusAssigned,
        showAsDialog = true
    )
}

@Composable
private fun ConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    cancelText: String,
    confirmText: String,
    icon: ImageVector,
    accentColor: Color,
    showAsDialog: Boolean
) {
    val content: @Composable () -> Unit = {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = SurfaceWhite,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.large, vertical = MaterialTheme.dimensions.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.height(MaterialTheme.dimensions.small))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(MaterialTheme.dimensions.small))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(MaterialTheme.dimensions.large))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(cancelText)
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text(confirmText, color = SurfaceWhite)
                    }
                }
            }
        }
    }

    if (showAsDialog) Dialog(onDismissRequest = onDismiss, content = content) else content()
}

@Preview(showBackground = true)
@Composable
private fun DeleteConfirmationDialogPreview() {
    BTSAllotTheme {
        ConfirmationDialog(
            onDismiss = {}, onConfirm = {}, title = "Delete Duty",
            message = "Are you sure you want to delete this duty?",
            cancelText = "Cancel", confirmText = "Delete", icon = Icons.Outlined.Delete,
            accentColor = LogoutRed, showAsDialog = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateApplyConfirmationDialogPreview() {
    BTSAllotTheme {
        ConfirmationDialog(
            onDismiss = {}, onConfirm = {}, title = "Confirm",
            message = "Are you sure you want to continue?", cancelText = "Cancel",
            confirmText = "Confirm", icon = Icons.Filled.CheckCircle,
            accentColor = StatusAssigned, showAsDialog = false
        )
    }
}

