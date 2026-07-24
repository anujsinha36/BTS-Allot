package com.example.btsallot.domain.utils


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