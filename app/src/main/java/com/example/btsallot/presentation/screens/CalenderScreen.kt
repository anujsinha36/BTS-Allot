package com.example.btsallot.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.btsallot.presentation.theme.BTSAllotTheme
import com.kizitonwose.calendar.compose.*
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalenderScreen(){
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(6) }
    val endMonth = remember { currentMonth.plusMonths(1) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    val daysOfWeek = remember { daysOfWeek() }


    val month = currentMonth.month.toString()
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )
    Column{
      //  DaysOfWeekTitle(daysOfWeek)
        Text(text = month)
        HorizontalCalendar(
            state = state,
            dayContent = { Day(it, {}) },
            monthHeader = {
                DaysOfWeekTitle(daysOfWeek)
            }
        )
    }

}

@Composable
fun Day(day: CalendarDay, onClick: (CalendarDay) -> Unit){
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickable(
                enabled = day.position == DayPosition.MonthDate, // Only month-dates are clickable
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (day.position == DayPosition.MonthDate) Color.Black else Color.Gray
        )
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>){
    Row(
        modifier = Modifier.fillMaxWidth()
    ){
        for (dayOfWeek in daysOfWeek){
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            )
        }

    }
}






@Preview(showBackground = true)
@Composable
fun PreviewCalenderScreen(){
    BTSAllotTheme {
        CalenderScreen()
    }
}