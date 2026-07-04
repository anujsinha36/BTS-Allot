package com.example.btsallot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.btsallot.presentation.screens.home.HomeScreen
import com.example.btsallot.presentation.screens.authenticate.LoginScreen
import com.example.btsallot.presentation.screens.calendar.CalenderScreen
import com.example.btsallot.presentation.screens.duty.DialogSheet
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun NavGraph(){
    val navController = rememberNavController()
    val auth = Firebase.auth.currentUser

    val userScreen = if (auth != null){
        Screens.CalendarScreen
    }
    else Screens.AuthScreen


    NavHost(navController = navController, startDestination = userScreen) {

        composable<Screens.AuthScreen> {
            LoginScreen(
                onSignInSuccess = {navController.navigate(Screens.CalendarScreen){
                    popUpTo(Screens.AuthScreen) {
                        inclusive = true
                    }
                } }
            )
        }

        composable<Screens.HomeScreen> {
            HomeScreen()
        }

        composable<Screens.CalendarScreen> {
            CalenderScreen(
                onDateClicked = {navController.navigate(Screens.CreateDutyScreen(it))}
            )
        }

        composable<Screens.CreateDutyScreen> {
            val args = it.toRoute<Screens.CreateDutyScreen>()
            DialogSheet(date = args.date,
                onSave = {navController.navigate(Screens.CalendarScreen)},
                onCancel = {navController.navigate(Screens.CalendarScreen)}
                )
        }
    }
}

// Once done, start with UI and complete admin duty creation

