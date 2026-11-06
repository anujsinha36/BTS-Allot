package com.example.btsallot.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.presentation.designsystem.buttons.PrimaryButton
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.Blue600
import com.example.btsallot.presentation.theme.Divider
import com.example.btsallot.presentation.theme.BlueSurface
import com.example.btsallot.presentation.theme.PositionsAvailableBg
import com.example.btsallot.presentation.theme.PositionsAvailableText
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextPrimary
import com.example.btsallot.presentation.theme.TextSecondary

data class DutyDetails(
    val title: String,
    val positionsAvailableLabel: String,
    val date: String,
    val timeRange: String,
    val venueName: String,
    val venueAddress: String,
    val volunteersRequired: Int,
    val notes: String
)

private val sampleDutyDetails = DutyDetails(
    title = "Morning Meeting Duty",
    positionsAvailableLabel = "1 of 2 positions available",
    date = "Sunday, 12 July 2025",
    timeRange = "09:00 AM - 11:00 AM",
    venueName = "Bangalore Centre",
    venueAddress = "12th Main, JP Nagar, Bengaluru",
    volunteersRequired = 2,
    notes = "Please be 10 mins early."
)

@Composable
fun DutyDetailsScreen(
    dutyId: String,
    details: DutyDetails = sampleDutyDetails,
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onApplyClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Blue600)
                }
                Text(
                    text = "Duty Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                IconButton(onClick = onMoreClick) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More options", tint = TextPrimary)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BlueSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Groups,
                    contentDescription = null,
                    tint = Blue600,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = details.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PositionsAvailableBg)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = details.positionsAvailableLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = PositionsAvailableText
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    InfoRow(icon = Icons.Filled.CalendarMonth, title = details.date)
                  //  RowDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoRow(icon = Icons.Filled.AccessTime, title = details.timeRange)
                  //  RowDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoRow(
                        icon = Icons.Filled.LocationOn,
                        title = details.venueName,
                        subtitle = details.venueAddress
                    )
                  //  RowDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoRow(
                        icon = Icons.Filled.Groups,
                        title = "Volunteers Required",
                        subtitle = details.volunteersRequired.toString()
                    )
                   // RowDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    InfoRow(
                        icon = Icons.Filled.Notes,
                        title = "Notes",
                        subtitle = details.notes
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                onClick = onApplyClick,
                text = "Apply for this Duty",
                enabled = true,
            )

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BlueSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Shield, contentDescription = null, tint = Blue600)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Once applied, your name will be added to list of BTS for this meeting",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, contentDescription = null, tint = Blue600, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun RowDivider() {
    Divider(color = Divider, thickness = 1.dp)
}

@Preview(showBackground = true)
@Composable
private fun DutyDetailsScreenPreview() {
    BTSAllotTheme { DutyDetailsScreen(
        dutyId = ""
    ) }
}
