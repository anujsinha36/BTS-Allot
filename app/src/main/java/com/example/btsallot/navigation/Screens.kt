package com.example.btsallot.navigation

import kotlinx.serialization.Serializable
import java.util.Date

sealed class Screens {

    @Serializable
    object AuthScreen : Screens()

    @Serializable
    object HomeScreen : Screens()

    @Serializable
    object CalendarScreen : Screens()

    @Serializable
    data class CreateDutyScreen(
        val date: String
    ) : Screens()

}

