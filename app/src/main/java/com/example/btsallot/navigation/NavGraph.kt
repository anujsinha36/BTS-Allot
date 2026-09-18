package com.example.btsallot.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.btsallot.presentation.screens.home.HomeScreen
import com.example.btsallot.presentation.screens.authenticate.LoginScreen
import com.example.btsallot.presentation.screens.calendar.AdminCalendarScreenContainer
import com.example.btsallot.presentation.screens.calendar.BTSCalendarScreenContainer
import com.example.btsallot.presentation.screens.duty.CreateDutyScreenContainer
import com.example.btsallot.presentation.viewmodels.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun NavGraph(){
    val navController = rememberNavController()
    val authCurrentUser = Firebase.auth.currentUser
    val authViewModel: AuthViewModel = hiltViewModel()

    val firstScreen = if (authCurrentUser != null){
        Screens.CalendarScreen
    }
    else Screens.AuthScreen


    NavHost(navController = navController, startDestination = firstScreen) {

        composable<Screens.AuthScreen> {
            LoginScreen(
                onSignInSuccess = {navController.navigate(Screens.CalendarScreen){
                    popUpTo(Screens.AuthScreen) {
                        inclusive = true
                    }
                } },
                viewModel = authViewModel
            )
        }

        composable<Screens.HomeScreen> {
            HomeScreen()
        }

        composable<Screens.CalendarScreen> {
            AdminCalendarScreenContainer(
                onDateClicked = {navController.navigate(Screens.CreateDutyScreen(it.toString()))},
                onCreateTemplate = {navController.navigate(Screens.TemplateScreen)},
                onBtsScreen = {navController.navigate(Screens.BTSCalendarScreen)}
            )
        }
        composable<Screens.BTSCalendarScreen> {
            BTSCalendarScreenContainer(
                authViewModel = authViewModel
            )
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


//currentUser is already here in Navgraph, do we also need to call in BTSContainer