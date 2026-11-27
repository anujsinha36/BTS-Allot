package com.example.btsallot.navigation

import kotlinx.serialization.Serializable

sealed class
Screens {

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
    object MyDutiesScreen: Screens()

   @Serializable
    object ProfileScreen: Screens()

    @Serializable
    object DashboardScreen: Screens()

    @Serializable
    object VolunteerListScreen: Screens()

    @Serializable
    data class AdminDutyDetailsScreen(
        val dutyId: String
    ): Screens()

    @Serializable
    data class BTSDetailsScreen(
        val dutyId: String
    ): Screens()

    @Serializable
    data class CreateDutyScreen(
        val date: String
    ) : Screens()

}

