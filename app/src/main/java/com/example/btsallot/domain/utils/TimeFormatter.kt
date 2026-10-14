package com.example.btsallot.domain.utils

import androidx.compose.ui.text.intl.Locale
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.text.format


fun toMinutes(hour: Int, minute: Int): Int{
    return hour * 60 + minute
}

fun fromMinutes(totalMinutes: Int): String{
    val hour = totalMinutes/60
    val mins = totalMinutes%60

    return formatTime(hour,mins)
}

fun formatTime(hour: Int, minute: Int): String {
    val isPm = hour >= 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    val period = if (isPm) "PM" else "AM"

    return "%02d:%02d %s".format(displayHour, minute, period)
}

fun String.toLocalDate(): LocalDate = LocalDate.parse(this)

fun String.toDayOfWeek(): String = toLocalDate()
    .format(DateTimeFormatter.ofPattern("EEE", java.util.Locale.getDefault()))

fun String.toDayOfMonth(): String =
    toLocalDate().dayOfMonth.toString()

fun String.toMonth(): String = toLocalDate()
    .format(DateTimeFormatter.ofPattern("MMM", java.util.Locale.getDefault()))