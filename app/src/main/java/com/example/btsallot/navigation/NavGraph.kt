package com.example.btsallot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.btsallot.presentation.screens.home.HomeScreen
import com.example.btsallot.presentation.screens.authenticate.LoginScreen
import com.example.btsallot.presentation.screens.calendar.AdminCalenderScreen
import com.example.btsallot.presentation.screens.calendar.BTSCalendarScreenContainer
import com.example.btsallot.presentation.screens.calendar.BTSCalenderScreen
import com.example.btsallot.presentation.screens.duty.CreateDutyScreenContainer
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


    NavHost(navController = navController, startDestination = Screens.BTSCalendarScreen) {

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
            AdminCalenderScreen(
                onDateClicked = {navController.navigate(Screens.CreateDutyScreen(it.toString()))},
                onCreateTemplate = {navController.navigate(Screens.TemplateScreen)}
            )
        }
        composable<Screens.BTSCalendarScreen> {
            BTSCalendarScreenContainer()
        }

        composable<Screens.TemplateScreen> {
            CreateDutyScreenContainer(
                onSaveClick = {
                    navController.navigate(Screens.CalendarScreen)
                },
                onBackClick = {
                    navController.navigate(Screens.CalendarScreen)
                },
                isTemplate = true,
                dateFromCalendar = null
            )
        }

        composable<Screens.CreateDutyScreen> {
            val args = it.toRoute<Screens.CreateDutyScreen>()
//            DialogSheet(date = args.date,
//                onSave = {navController.navigate(Screens.CalendarScreen)},
//                onCancel = {navController.navigate(Screens.CalendarScreen)}
//                )
            CreateDutyScreenContainer(
                dateFromCalendar = java.time.LocalDate.parse(args.date),
                onSaveClick = {
                    navController.navigate(Screens.CalendarScreen)
                },
                onBackClick = {
                    navController.navigate(Screens.CalendarScreen)
                },
                isTemplate = false
            )
        }
    }
}


