package com.example.btsallot.presentation.screens.duty

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.utils.fromMinutes
import com.example.btsallot.domain.utils.toDayOfMonth
import com.example.btsallot.domain.utils.toDayOfWeek
import com.example.btsallot.domain.utils.toMonth
import com.example.btsallot.presentation.designsystem.scaffold.AppScaffold
import com.example.btsallot.presentation.designsystem.scaffold.BottomNavItem
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.BackgroundLight
import com.example.btsallot.presentation.theme.Blue600
import com.example.btsallot.presentation.theme.BlueLight
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextPrimary
import com.example.btsallot.presentation.theme.TextSecondary
import com.example.btsallot.presentation.theme.dimensions

@Composable
fun MyDutiesScreen(
    upcomingDuties: List<Duty>,
    pastDuties: List<Duty> = emptyList(),
    onNavigation: (BottomNavItem) -> Unit = {},
    onDeleteApplication: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    AppScaffold(
        title = "My Duties",
        currentScreen = BottomNavItem.Duties,
        onNavigate = onNavigation
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(MaterialTheme.dimensions.medium)
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.medium))
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = BackgroundLight,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text("Upcoming",
                            style = if (selectedTabIndex == 0) MaterialTheme.typography.labelLarge
                            else MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Past", style = if (selectedTabIndex == 1) MaterialTheme.typography.labelLarge
                    else MaterialTheme.typography.labelMedium) }
                )
            }
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.medium))

            val listForTab = if (selectedTabIndex == 0) upcomingDuties else pastDuties

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = MaterialTheme.dimensions.medium)
            ) {
                if (listForTab.isNotEmpty()){
                    items(listForTab) { duty ->
                        MyDutyCard(duty, onDeleteApplication)
                        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.medium))
                    }
                }
                else{
                    item {
                        Text(text = "No duties found",
                            style = MaterialTheme.typography.labelMedium,
                            color = Blue600, modifier = Modifier.padding(vertical = MaterialTheme.dimensions.medium))
                    }
                }
            }
        }
    }
}

@Composable
private fun MyDutyCard(duty: Duty, onDelete: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimensions.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(BlueLight)
                    .padding(horizontal = MaterialTheme.dimensions.medium, vertical = MaterialTheme.dimensions.small),
                    horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = duty.date.toDayOfWeek(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.extraSmall))
                Text(text = duty.date.toDayOfMonth(), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.extraSmall))
                Text(text = duty.date.toMonth(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(modifier = Modifier.width(MaterialTheme.dimensions.medium))
            Column(modifier = Modifier.weight(1f),verticalArrangement = Arrangement.SpaceEvenly) {
                val startTime = duty.startMinutes
                val endTime = duty.endMinutes
                Text(text = duty.meetingName, style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary)
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
                Text(text = "${fromMinutes(startTime)} - ${fromMinutes(endTime)}", style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary)
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
                Text(text = duty.location, style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary)
            }

            IconButton(onClick = {
                onDelete()
            },) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete duty",
                    tint = Color.Red.copy(0.6f)
                )
            }
        }
    }
}

@Composable
private fun CannotMakeItCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlueLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Can't make it? Let the admin know as early as possible.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyDutiesScreenPreview() {
    BTSAllotTheme {
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
        MyDutiesScreen(
        upcomingDuties = listOf(duty1)
    ) }
}
