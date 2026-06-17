package com.example.btsallot.navigation

import kotlinx.serialization.Serializable

sealed class Screens {

    @Serializable
    object AuthScreen: Screens()

    @Serializable
    object HomeScreen: Screens()
}
