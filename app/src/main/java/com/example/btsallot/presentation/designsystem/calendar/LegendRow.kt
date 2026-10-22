package com.example.btsallot.presentation.designsystem.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.btsallot.presentation.theme.StatusApplied
import com.example.btsallot.presentation.theme.StatusAvailable
import com.example.btsallot.presentation.theme.StatusFull
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.dimensions

@Composable
fun LegendRow() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.dimensions.large,
                    vertical = MaterialTheme.dimensions.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendDot(StatusAvailable, "Available")
            LegendDot(StatusApplied, "Applied")
            //  LegendDot(StatusAssigned, "Assigned")
            LegendDot(StatusFull, "Full")
        }
    }
}