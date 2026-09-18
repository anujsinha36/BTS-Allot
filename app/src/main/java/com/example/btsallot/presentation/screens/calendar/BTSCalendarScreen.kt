package com.example.btsallot.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.domain.utils.fromMinutes
import com.example.btsallot.presentation.designsystem.buttons.LegendDot
import com.example.btsallot.presentation.designsystem.calendar.Day
import com.example.btsallot.presentation.designsystem.calendar.DaysOfWeekTitle
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.Blue600
import com.example.btsallot.presentation.theme.BlueLight
import com.example.btsallot.presentation.theme.FullText
import com.example.btsallot.presentation.theme.StatusApplied
import com.example.btsallot.presentation.theme.StatusAvailable
import com.example.btsallot.presentation.theme.StatusFull
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextPrimary
import com.example.btsallot.presentation.theme.TextSecondary
import com.example.btsallot.presentation.theme.TextTertiary
import com.kizitonwose.calendar.compose.*
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BTSCalenderScreen(
    duties: List<Duty>,
    onDateClicked: (LocalDate) -> Unit = {},
    onDutyApplyClicked: (Duty) -> Unit ={}
){
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(6) }
    val endMonth = remember { currentMonth.plusMonths(1) }
    val daysOfWeek = remember { daysOfWeek() }
    val selectedDate = remember { mutableStateOf<CalendarDay?>(null) }


    // Groups the list of duties into a Map (folders) for instant lookup by date.
    // 'remember(duties)' ensures the sorting only happens when the duty list changes.
    val dutiesByDate = remember(duties) {
        duties.groupBy { it.date }
    }

    //see if this can be placed somewhere else
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    }


    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfRow
    )
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = state.firstVisibleMonth



    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 10.dp),
    ){

        Spacer(modifier = Modifier.padding(vertical = 40.dp))
        CalendarTitle(
            currentMonth = visibleMonth.yearMonth,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            goToPrevious = {
                coroutineScope.launch {
                    state.animateScrollToMonth(visibleMonth.yearMonth.previousMonth)
                }
            },
            goToNext = {
                coroutineScope.launch {
                    state.animateScrollToMonth(visibleMonth.yearMonth.nextMonth)

                }
            }
        )

        HorizontalCalendar(
            state = state,
            dayContent = {day->
                val dayDuties = dutiesByDate[day.date.toString()] ?: emptyList()
                Day(day, selectedDate.value == day,
                    duties = dayDuties,
                    onClick = {clicked->
                    selectedDate.value = clicked
                    onDateClicked(clicked.date)
                    })
            },
            monthHeader = {
                DaysOfWeekTitle(daysOfWeek)
            }
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        LegendRow()
        Spacer(modifier = Modifier.padding(vertical = 20.dp))

        val selectedDateDuties = selectedDate.value?.let { clicked->
            dutiesByDate[clicked.date.toString()]
        }?: emptyList()

         selectedDate.value?.let{
            if (selectedDateDuties.isNotEmpty()){
                selectedDateDuties.forEach { duty->
                    DutyListCard(duty = duty, onDutyApplyClick = {onDutyApplyClicked(duty)}
                    )
                }
            }
            else{
                Text(
                    text = "No duties on this date",
                    style = MaterialTheme.typography.labelMedium, color = Blue600
                )
            }
        }
    }
}

@Composable
private fun LegendRow() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendDot(StatusAvailable, "Available")
            LegendDot(StatusApplied, "Applied")
            //  LegendDot(StatusAssigned, "Assigned")
            LegendDot(StatusFull, "Full")
        }
    }
}

@Composable
fun DutyListCard(duty: Duty,
                 onDutyApplyClick: (Duty) -> Unit,
                 modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp)
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
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val startTime = duty.startMinutes
                val endTime = duty.endMinutes
                Text(
                    text = "${fromMinutes(startTime)} - ${fromMinutes(endTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = duty.meetingName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = duty.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                val status = duty.btsRequired - duty.btsReservedCount
                if (status < 1) {
                    Text(
                        text = "${duty.btsReservedCount}/${duty.btsRequired}",
                        style = MaterialTheme.typography.labelMedium,
                        color = FullText
                    )
                    Text(text = "filled", style = MaterialTheme.typography.bodySmall, color = FullText)
                } else {
                    Text(
                        text = "${duty.btsReservedCount}/${duty.btsRequired}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Blue600
                    )
                    Text(
                        text = "filled",
                        style = MaterialTheme.typography.bodySmall,
                        color = Blue600
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { onDutyApplyClick(duty) },
                        shape = RoundedCornerShape(10.dp),
                       // colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Apply", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBTSCalenderScreen(){
    BTSAllotTheme {
        BTSCalenderScreen(
            duties = emptyList(),
        )
    }
}


// provide logic for updating BTSReservedCount based on Application:
// Might need to create a update function in repository to update duty collection's paramter BTSReserved
//best approach if we have same viewmodel across multiple screens?
