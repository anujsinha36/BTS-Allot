package com.example.btsallot.presentation.designsystem.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.btsallot.data.model.Duty
import com.example.btsallot.data.room.DutyEntity
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition

@Composable
fun Day(day: CalendarDay,
        isSelected: Boolean,
       // duties: List<Duty> = emptyList(),
        duties: List<DutyEntity> = emptyList(),
        onClick: (CalendarDay) -> Unit){
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(6.dp)
            .background(color = if (isSelected) Color.Yellow else Color.Transparent)
            .clickable(
                enabled = day.position == DayPosition.MonthDate, // Only month-dates are clickable
                onClick = { onClick(day) },

                ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            val textColor = when (day.position) {
                // Color.Unspecified will use the default text color from the current theme
                DayPosition.MonthDate -> if (isSelected) Color.Black else Color.Unspecified
                DayPosition.InDate, DayPosition.OutDate -> Color.Gray
            }

            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor
            )
            Spacer(modifier = Modifier.height(5.dp))

            Box(modifier = Modifier.size(8.dp).clip(CircleShape)
                .background(if (duties.isNotEmpty()) Color.Blue else Color.Transparent)
            )



        }
    }
    }