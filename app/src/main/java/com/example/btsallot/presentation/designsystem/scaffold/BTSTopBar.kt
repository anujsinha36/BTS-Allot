package com.example.btsallot.presentation.designsystem.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.TextPrimary

@Composable
fun BTSTopBar(
    title: String? = null,
    onBackClick: (() -> Unit)? = null

){
    Surface (modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() //Pushes elements below the device's System Status Bar (clock/battery area).
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null){
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                    )
                }
            }
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTopBar(){
    BTSAllotTheme {
        BTSTopBar(
            title = "Duties",
            onBackClick = {}
        )
    }

}