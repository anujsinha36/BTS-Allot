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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.domain.model.User
import com.example.btsallot.presentation.designsystem.scaffold.AppScaffold
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.BackgroundLight
import com.example.btsallot.presentation.theme.Blue600
import com.example.btsallot.presentation.theme.BlueLight
import com.example.btsallot.presentation.theme.BlueSurface
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextPrimary
import com.example.btsallot.presentation.theme.TextSecondary
import com.example.btsallot.presentation.theme.TextTertiary
import com.example.btsallot.presentation.viewmodels.VolunteerFilter
import com.example.btsallot.presentation.viewmodels.VolunteerItemState
import com.example.btsallot.presentation.viewmodels.VolunteersUiState
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun VolunteersListScreen(
    uiState: VolunteersUiState,
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterSelected: (VolunteerFilter) -> Unit = {},
    onMonthSelected: (YearMonth) -> Unit = {},
    onVolunteerClick: (String) -> Unit = {}
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
                text = "Volunteers",
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

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BlueSurface)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (uiState.searchQuery.isEmpty()) {
                        Text(
                            text = "Search volunteers...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary
                        )
                    }
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChanged,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChanged("") },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextTertiary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChipItem(
                    label = "All (${uiState.allCount})",
                    isSelected = uiState.selectedFilter == VolunteerFilter.ALL,
                    onClick = { onFilterSelected(VolunteerFilter.ALL) }
                )
            }
            item {
                FilterChipItem(
                    label = "With Duties (${uiState.withDutiesCount})",
                    isSelected = uiState.selectedFilter == VolunteerFilter.WITH_DUTIES,
                    onClick = { onFilterSelected(VolunteerFilter.WITH_DUTIES) }
                )
            }
            item {
                FilterChipItem(
                    label = "No Duties (${uiState.noDutiesCount})",
                    isSelected = uiState.selectedFilter == VolunteerFilter.NO_DUTIES,
                    onClick = { onFilterSelected(VolunteerFilter.NO_DUTIES) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Volunteer List
        if (uiState.volunteers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "No volunteers found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.volunteers, key = { it.user.uid }) { volunteerItem ->
                    VolunteerCard(
                        item = volunteerItem,
                        onClick = { onVolunteerClick(volunteerItem.user.uid) }
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
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) Blue600 else BlueSurface
    val textColor = if (isSelected) SurfaceWhite else TextPrimary
    val fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = fontWeight
        )
    }
}

@Composable
private fun VolunteerCard(
    item: VolunteerItemState,
    onClick: () -> Unit
) {
    val initials = getInitials(item.user.name)

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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Blue600
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Duty Count
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.dutyCount.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (item.dutyCount == 1) "duty" else "duties",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Chevron Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun getInitials(name: String): String {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return "V"
    val parts = trimmed.split("\\s+".toRegex())
    return when {
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts[0].first()}${parts.last().first()}".uppercase()
    }
}

@Preview(showBackground = true)
@Composable
fun VolunteersListScreenPreview() {
    BTSAllotTheme {
        VolunteersListScreen(
            uiState = VolunteersUiState(
                selectedYearMonth = YearMonth.of(2025, 7),
                searchQuery = "",
                selectedFilter = VolunteerFilter.ALL,
                allCount = 50,
                withDutiesCount = 34,
                noDutiesCount = 16,
                volunteers = listOf(
                    VolunteerItemState(
                        user = User(
                            uid = "1",
                            name = "Anuj Sinha",
                            email = "anuj.sinha@gmail.com",
                            role = "VOLUNTEER",
                            photoUrl = null
                        ),
                        dutyCount = 1
                    ),
                    VolunteerItemState(
                        user = User(
                            uid = "2",
                            name = "Priya Rao",
                            email = "priya.rao@gmail.com",
                            role = "VOLUNTEER",
                            photoUrl = null
                        ),
                        dutyCount = 2
                    ),
                    VolunteerItemState(
                        user = User(
                            uid = "3",
                            name = "Rahul Kumar",
                            email = "rahul.kumar@gmail.com",
                            role = "VOLUNTEER",
                            photoUrl = null
                        ),
                        dutyCount = 1
                    )
                )
            )
        )
    }
}
