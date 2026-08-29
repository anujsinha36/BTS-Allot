package com.example.btsallot.navigation

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.Date

sealed class Screens {

    @Serializable
    object AuthScreen : Screens()

    @Serializable
    object HomeScreen : Screens()

    @Serializable
    object CalendarScreen : Screens()

    @Serializable
    object TemplateScreen: Screens()

    @Serializable
    object BTSCalendarScreen : Screens()

    @Serializable
    data class CreateDutyScreen(
        val date: String
    ) : Screens()

}

