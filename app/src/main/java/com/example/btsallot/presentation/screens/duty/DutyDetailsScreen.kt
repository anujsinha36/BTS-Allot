package com.example.btsallot.presentation.screens.duty

import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.model.DutyApplication
import com.example.btsallot.domain.utils.fromMinutes
import com.example.btsallot.presentation.designsystem.buttons.PrimaryButton
import com.example.btsallot.presentation.designsystem.scaffold.AppScaffold
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.Blue600
import com.example.btsallot.presentation.theme.Divider
import com.example.btsallot.presentation.theme.BlueSurface
import com.example.btsallot.presentation.theme.PositionsAvailableBg
import com.example.btsallot.presentation.theme.PositionsAvailableText
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextPrimary
import com.example.btsallot.presentation.theme.TextSecondary
import com.example.btsallot.presentation.theme.dimensions
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.Locale

@Composable
fun DutyDetailsScreen(
    duty: Duty,
    applications: List<DutyApplication> = emptyList(),
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onApplyClick: () -> Unit = {},
    isAdmin: Boolean = false
) {
    AppScaffold(
        title = "Duty Details",
        onBackClick = onBackClick,
        onOptionsClick = if (isAdmin) onMoreClick else null
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize().padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = MaterialTheme.dimensions.medium)
                .verticalScroll(rememberScrollState())

        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(68.dp)
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
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.medium))
            Text(
                text = duty.meetingName,
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
                    text = "${duty.btsReservedCount} out of ${duty.btsRequired} positions filled",
                    style = MaterialTheme.typography.labelMedium,
                    color = PositionsAvailableText
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.large))

            val dateFormatter =
                remember { DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault()) }
            val formattedDate = remember(duty.date, dateFormatter) {
                runCatching { LocalDate.parse(duty.date).format(dateFormatter) }
                    .getOrDefault(duty.date)
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.dimensions.medium,
                        vertical = MaterialTheme.dimensions.small
                    )
                ) {
                    InfoRow(
                        icon = Icons.Filled.CalendarMonth,
                        title = formattedDate
                    )
                    //  RowDivider()
                    Spacer(modifier = Modifier.height(MaterialTheme.dimensions.extraSmall))
                    InfoRow(
                        icon = Icons.Filled.AccessTime,
                        title = "${fromMinutes(duty.startMinutes)} - ${fromMinutes(duty.endMinutes)}"
                    )
                    //  RowDivider()
                    Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
                    InfoRow(
                        icon = Icons.Filled.LocationOn,
                        title = duty.location,
                    )
                    //  RowDivider()
                    Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
                    InfoRow(
                        icon = Icons.Filled.Groups,
                        title = "Volunteers Required",
                        subtitle = duty.btsRequired.toString()
                    )
                    // RowDivider()
                    Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))

                    InfoRow(
                        icon = Icons.Filled.Notes,
                        title = "Notes",
                        subtitle = duty.notes?.takeIf { it.isNotBlank() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.large))

            if (!isAdmin) {
                PrimaryButton(
                    onClick = onApplyClick,
                    text = "Apply for this Duty",
                    enabled = true,
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.medium))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlueSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(MaterialTheme.dimensions.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = Blue600)
                        Spacer(modifier = Modifier.width(MaterialTheme.dimensions.medium))
                        Text(
                            text = "Once applied, your name will be added to list of BTS roster for this meeting",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.large))
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlueSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.dimensions.medium,
                            vertical = MaterialTheme.dimensions.small
                        )
                    ) {
                        Text(
                            text = "Applied Volunteers: ${applications.size}",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.extraSmall))
                        HorizontalDivider(color = Divider, thickness = 2.dp)

                        applications.forEachIndexed { index, applicant ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val initials = applicant.userName
                                    .split(" ")
                                    .filter { it.isNotBlank() }
                                    .take(2)
                                    .joinToString("") { it.first().uppercase() }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(SurfaceWhite),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Blue600
                                    )
                                }

                                Spacer(modifier = Modifier.width(MaterialTheme.dimensions.medium))
                                Text(
                                    text = applicant.userName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                            if (index < applications.lastIndex) {
                                HorizontalDivider(
                                    color = Divider,
                                    thickness = 2.dp
                                )
                            }
                        }
                    }
                }

                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
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
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextPrimary, )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DutyDetailsScreenPreview() {
    val duty1 = Duty(
        id = "2026-09-19_City_Level_Daimoku",
        date = "2026-09-19",
        meetingName = "City Level Daimoku",
        startMinutes = 600,
        endMinutes = 780,
        btsRequired = 2,
        btsReservedCount = 1,
        location = "BLR Kaikan",
        notes = null
    )
    BTSAllotTheme { DutyDetailsScreen(
        duty = duty1,
        isAdmin = false
    ) }
}



