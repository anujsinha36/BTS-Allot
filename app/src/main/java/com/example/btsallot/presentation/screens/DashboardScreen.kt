package com.example.btsallot.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.utils.fromMinutes
import com.example.btsallot.domain.utils.toDayOfMonth
import com.example.btsallot.domain.utils.toDayOfWeek
import com.example.btsallot.domain.utils.toMonth
import com.example.btsallot.presentation.designsystem.scaffold.AppScaffold
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.BackgroundLight
import com.example.btsallot.presentation.theme.Blue600
import com.example.btsallot.presentation.theme.BlueLight
import com.example.btsallot.presentation.theme.BlueSurface
import com.example.btsallot.presentation.theme.PositionsAvailableBg
import com.example.btsallot.presentation.theme.PositionsAvailableText
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextPrimary
import com.example.btsallot.presentation.theme.TextSecondary
import com.example.btsallot.presentation.theme.TextTertiary
import com.example.btsallot.presentation.viewmodels.DashboardUiState
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onMonthSelected: (YearMonth) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onDutyClick: (String) -> Unit = {}
) {
    var monthMenuExpanded by remember { mutableStateOf(false) }

    AppScaffold() { paddingValues ->

    Column(
        modifier = Modifier
            .fillMaxSize().padding(paddingValues)
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Month Selector Pill
            Box {
                val formattedMonth = uiState.selectedYearMonth.format(
                    DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BlueSurface)
                        .clickable { monthMenuExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedMonth,
                        style = MaterialTheme.typography.labelLarge,
                        color = Blue600,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Month",
                        tint = Blue600,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = monthMenuExpanded,
                    onDismissRequest = { monthMenuExpanded = false }
                ) {
                    val currentYearMonth = YearMonth.now()
                    (-6..6).forEach { offset ->
                        val ym = currentYearMonth.plusMonths(offset.toLong())
                        val ymLabel =
                            ym.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
                        DropdownMenuItem(
                            text = { Text(ymLabel) },
                            onClick = {
                                onMonthSelected(ym)
                                monthMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2x2 Stat Cards Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DateRange,
                    iconBg = BlueLight,
                    iconTint = Blue600,
                    count = uiState.totalDutiesCount.toString(),
                    label = "Total Duties"
                )
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Groups,
                    iconBg = BlueLight,
                    iconTint = Blue600,
                    count = uiState.totalApplicationsCount.toString(),
                    label = "Total Applications"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    iconBg = PositionsAvailableBg,
                    iconTint = PositionsAvailableText,
                    count = uiState.dutiesWithApplicationsCount.toString(),
                    label = "Duties with\nApplications"
                )
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.People,
                    iconBg = BlueLight,
                    iconTint = Blue600,
                    count = uiState.activeVolunteersCount.toString(),
                    label = "Active Volunteers"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Upcoming Duties Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Upcoming Duties",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelLarge,
                color = Blue600,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List of Upcoming Duties
        if (uiState.upcomingDuties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No upcoming duties available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.upcomingDuties, key = { it.id }) { duty ->
                    UpcomingDutyCard(
                        duty = duty,
                        onClick = { onDutyClick(duty.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
}

@Composable
private fun DashboardStatCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFF0F4FA), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = count,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun UpcomingDutyCard(
    duty: Duty,
    onClick: () -> Unit
) {
    val isFull = duty.btsReservedCount >= duty.btsRequired

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFF0F4FA), RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Badge Box
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BlueSurface),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = duty.date.toDayOfWeek(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = duty.date.toDayOfMonth(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = duty.date.toMonth(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Duty Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = duty.meetingName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${fromMinutes(duty.startMinutes)} - ${fromMinutes(duty.endMinutes)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = duty.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Badge Pill
            val (badgeBg, badgeText, statusLabel) = if (isFull) {
                Triple(PositionsAvailableBg, PositionsAvailableText, "full")
            } else {
                Triple(BlueSurface, Blue600, "applied")
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeBg)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${duty.btsReservedCount}/${duty.btsRequired}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = badgeText
                    )
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = badgeText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    BTSAllotTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                selectedYearMonth = YearMonth.of(2025, 7),
                totalDutiesCount = 12,
                totalApplicationsCount = 38,
                dutiesWithApplicationsCount = 8,
                activeVolunteersCount = 24,
                upcomingDuties = listOf(
                    Duty(
                        id = "1",
                        date = "2025-07-12",
                        meetingName = "Morning Meeting Duty",
                        startMinutes = 540,
                        endMinutes = 660,
                        btsRequired = 2,
                        btsReservedCount = 1,
                        location = "Bangalore Centre",
                        notes = null
                    ),
                    Duty(
                        id = "2",
                        date = "2025-07-15",
                        meetingName = "Study Meeting Duty",
                        startMinutes = 1140,
                        endMinutes = 1260,
                        btsRequired = 2,
                        btsReservedCount = 2,
                        location = "Bangalore Centre",
                        notes = null
                    ),
                    Duty(
                        id = "3",
                        date = "2025-07-19",
                        meetingName = "Youth Discussion Duty",
                        startMinutes = 1110,
                        endMinutes = 1230,
                        btsRequired = 2,
                        btsReservedCount = 1,
                        location = "Bangalore Centre",
                        notes = null
                    )
                )
            )
        )
    }
}
