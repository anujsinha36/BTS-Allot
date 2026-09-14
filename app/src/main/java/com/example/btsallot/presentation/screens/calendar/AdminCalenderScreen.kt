package com.example.btsallot.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.btsallot.presentation.designsystem.buttons.PrimaryButton
import com.example.btsallot.presentation.designsystem.calendar.Day
import com.example.btsallot.presentation.designsystem.calendar.DaysOfWeekTitle
import com.example.btsallot.presentation.theme.BTSAllotTheme
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
    onDateClicked: (LocalDate) -> Unit = {},
    onCreateTemplate: ()-> Unit = {},
    onSync: () -> Unit = {},
    onBtsScreen: () -> Unit = {}
){
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(6) }
    val endMonth = remember { currentMonth.plusMonths(1) }
    val daysOfWeek = remember { daysOfWeek() }
    val selectedDate = remember { mutableStateOf<CalendarDay?>(null) }

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
        .background(Color.White)
        .padding(horizontal = 10.dp),
    ){

        Spacer(modifier = Modifier.padding(vertical = 40.dp))
        CalendarTitle(
            currentMonth = visibleMonth.yearMonth,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            isHorizontal = true,
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
                Day(day, selectedDate.value == day, onClick = {clicked->
                    selectedDate.value = clicked
                    onDateClicked(clicked.date)
                })
             },
            monthHeader = {
                DaysOfWeekTitle(daysOfWeek)
            }
        )
        Spacer(modifier = Modifier.padding(vertical = 50.dp))
        PrimaryButton(onClick = {onCreateTemplate()},
            text = "Create Template ->",
            enabled = true)

        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        PrimaryButton(
            onClick = {onBtsScreen()},
            text = "BTS Screen",
            enabled = true
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        //sync button
        PrimaryButton(onClick = {onSync()},
            text = "Sync",
            enabled = true)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminCalenderScreen(){
    BTSAllotTheme {
        AdminCalenderScreen()
    }
}