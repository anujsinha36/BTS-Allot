package com.example.btsallot.presentation.designsystem.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.utils.fromMinutes
import com.example.btsallot.domain.utils.isUpcomingOrOngoing
import com.example.btsallot.presentation.screens.calendar.BTSCalenderScreen
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.Blue600
import com.example.btsallot.presentation.theme.BlueLight
import com.example.btsallot.presentation.theme.FullText
import com.example.btsallot.presentation.theme.LogoutRed
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextPrimary
import com.example.btsallot.presentation.theme.TextSecondary
import com.example.btsallot.presentation.theme.TextTertiary
import com.example.btsallot.presentation.theme.dimensions

@Composable
fun DutyListCard(duty: Duty,
                 modifier: Modifier = Modifier,
                 onDutyClick: (String) -> Unit = {},
                 onDutyApplyClick: (Duty) -> Unit = {},
                 isAdmin: Boolean = false,
                 onEditClick: (Duty) -> Unit = {},
                 onDeleteClick: (Duty) -> Unit = {},
                 appliedDutyId: Set<String> = emptySet()
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.dimensions.extraSmall)
            .clickable {onDutyClick(duty.id)}
        ,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimensions.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Groups,
                    contentDescription = null,
                    tint = Blue600,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.dimensions.medium))
            Column(modifier = Modifier.weight(1f)) {
                val startTime = duty.startMinutes
                val endTime = duty.endMinutes
                Text(
                    text = "${fromMinutes(startTime)} - ${fromMinutes(endTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.extraSmall))
                Text(
                    text = duty.meetingName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.extraSmall))
                Text(
                    text = duty.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Column(horizontalAlignment = Alignment.End) {
                    val isFull = duty.btsReservedCount >= duty.btsRequired
                    val hasApplied = appliedDutyId.contains(duty.id)
                    val statusColor = if (isFull) FullText else Blue600

                    Text(
                        text = "${duty.btsReservedCount}/${duty.btsRequired}",
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor
                    )
                    Text(
                        text = "filled",
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))

                    if (isAdmin) {
                        // Admin Screen Actions: Edit & Delete Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.extraSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BlueLight)
                                    .clickable { onEditClick(duty) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit duty",
                                    tint = Blue600,
                                    modifier = Modifier.size(MaterialTheme.dimensions.medium)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFFEBEB))
                                    .clickable { onDeleteClick(duty) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "Delete duty",
                                    tint = LogoutRed,
                                    modifier = Modifier.size(MaterialTheme.dimensions.medium)
                                )
                            }
                        }
                    }
                    else{
                        // to check if duty date already passed
                        val applicationOpen = duty.isUpcomingOrOngoing()
                        Button(
                            onClick = { onDutyApplyClick(duty) },
                            shape = RoundedCornerShape(10.dp),
                            enabled = applicationOpen && !hasApplied && !isFull,
                            contentPadding = PaddingValues(
                                horizontal = MaterialTheme.dimensions.medium,
                                vertical = MaterialTheme.dimensions.extraSmall),
                            modifier = Modifier.height(32.dp)
                        ) {
                            val text = when{
                                hasApplied -> "Applied"
                                !applicationOpen -> "Closed"
                                else  -> "Apply"
                            }
                            Text(text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }


                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DutyListCardPreview(){
    BTSAllotTheme {
        val duty = Duty(
            id = "2026-10-30_City_Level_Daimoku",
            date = "2026-10-30",
            meetingName = "City Level Daimoku",
            startMinutes = 600,
            endMinutes = 780,
            btsRequired = 2,
            btsReservedCount = 1,
            location = "BLR Kaikan",
            notes = null
        )
        DutyListCard(
            duty = duty,
            onDutyApplyClick = {},
            appliedDutyId = emptySet(),
            isAdmin = true
        )
    }
}