package com.example.btsallot.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.btsallot.data.model.Duty
import com.example.btsallot.data.repository.AuthRepository
import com.example.btsallot.presentation.designsystem.buttons.PrimaryButton
import com.example.btsallot.presentation.designsystem.calendar.Day
import com.example.btsallot.presentation.designsystem.calendar.DaysOfWeekTitle
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.example.btsallot.presentation.theme.Indigo600
import com.example.btsallot.presentation.theme.SurfaceWhite
import com.example.btsallot.presentation.theme.TextPrimary
import com.example.btsallot.presentation.theme.TextSecondary
import com.example.btsallot.presentation.viewmodels.AuthViewModel
import com.kizitonwose.calendar.compose.*
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun BTSCalenderScreen(
    onDateClicked: (LocalDate) -> Unit = {},
    onCreateTemplate: ()-> Unit = {}
){
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(6) }
    val endMonth = remember { currentMonth.plusMonths(1) }
    val daysOfWeek = remember { daysOfWeek() }
    val selectedDate = remember { mutableStateOf<CalendarDay?>(null) }

    val context = LocalContext.current.applicationContext
    val viewModel: AuthViewModel = viewModel(
        factory = object :  ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(
                    repository = AuthRepository(context)
                ) as T
            }
        }
    )
    val duties = viewModel.duties.value
    LaunchedEffect(Unit) {
        viewModel.getDuties()
    }
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
        Spacer(modifier = Modifier.padding(vertical = 50.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewBTSCalenderScreen(){
    BTSAllotTheme {
        BTSCalenderScreen()
    }
}