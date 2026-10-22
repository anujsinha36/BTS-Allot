package com.example.btsallot.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.domain.model.Duty
import com.example.btsallot.presentation.designsystem.buttons.PrimaryButton
import com.example.btsallot.presentation.designsystem.calendar.CalendarTitle
import com.example.btsallot.presentation.designsystem.calendar.Day
import com.example.btsallot.presentation.designsystem.calendar.DaysOfWeekTitle
import com.example.btsallot.presentation.designsystem.calendar.LegendRow
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.Blue600
import com.example.btsallot.presentation.theme.StatusAvailable
import com.example.btsallot.presentation.theme.StatusFull
import com.example.btsallot.presentation.theme.dimensions
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
fun AdminCalenderScreen(
    duties: List<Duty>,
    onDateClicked: (LocalDate) -> Unit = {},
    onCreateTemplate: ()-> Unit = {},
    onBtsScreen: () -> Unit = {}
){
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(6) }
    val endMonth = remember { currentMonth.plusMonths(1) }
    val daysOfWeek = remember { daysOfWeek() }
    val selectedDate = remember { mutableStateOf<CalendarDay?>(null) }

    val dutiesByDate = remember(duties) {
        duties.groupBy { it.date }
    }

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

    //reacts only when month changes
    LaunchedEffect(visibleMonth.yearMonth) {
        selectedDate.value = null
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(horizontal = MaterialTheme.dimensions.medium),
    ){

        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.medium))
        CalendarTitle(
            currentMonth = visibleMonth.yearMonth,
            modifier = Modifier.padding(
                vertical = MaterialTheme.dimensions.small,
                horizontal = MaterialTheme.dimensions.extraSmall
            ),
            // isHorizontal = true,
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
            modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.small),
            state = state,
            dayContent = {day->
                val dayDuties = dutiesByDate[day.date.toString()] ?: emptyList()
                val dayColor = when {
                    // 1. If there are no duties at all
                    dayDuties.isEmpty() -> Color.Transparent

                    // 2. If the user has applied to ANY duty on this day (High Priority)
                  //  dayDuties.any { appliedDutyId.contains(it.id) } -> StatusApplied

                    // 3. If ALL duties for the day are full
                    dayDuties.all { it.btsReservedCount >= it.btsRequired } -> StatusFull

                    // 4. Otherwise, duties are available (hasDuty = true)
                    else -> StatusAvailable
                }
                Day(day, selectedDate.value == day,
                    duties = dayDuties,
                    onClick = {clicked->
                    selectedDate.value = clicked
                    onDateClicked(clicked.date)
                }, color = dayColor
                )
             },
            monthHeader = {
                DaysOfWeekTitle(daysOfWeek)
            }
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
        LegendRow()
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.large))
        selectedDate.value?.let { clicked ->
            val formattedDate = clicked.date.format(dateFormatter)
            val selectedDateDuties = dutiesByDate[clicked.date.toString()]

            if (selectedDateDuties.isNullOrEmpty()) {
                Text(
                    text = "No duties on this date",
                    style = MaterialTheme.typography.labelMedium,
                    color = Blue600
                )
            } else {
                Text(
                    text = "Duties on $formattedDate",
                    style = MaterialTheme.typography.labelMedium,
                    color = Blue600
                )
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))

                selectedDateDuties.forEach { duty ->
                    DutyListCard(
                        duty = duty,
                    )
                }
            }
        }
        PrimaryButton(onClick = {onCreateTemplate()},
            text = "Create Template",
            enabled = true)

        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.small))
        PrimaryButton(
            onClick = {onBtsScreen()},
            text = "BTS Screen",
            enabled = true
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        //sync button
//        PrimaryButton(onClick = {onSync()},
//            text = "Sync",
//            enabled = true)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminCalenderScreen(){
    BTSAllotTheme {
        val duty12 = Duty(
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
        AdminCalenderScreen(
            duties = listOf(duty12)
        )
    }
}

//Add create duty button and shift the functionality of create duty to that