package com.example.btsallot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.btsallot.presentation.screens.HomeScreen
import com.example.btsallot.presentation.screens.LoginScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun NavGraph(){
    val navController = rememberNavController()
    val auth = Firebase.auth.currentUser

    val userScreen = if (auth != null){
        Screens.HomeScreen
    }
    else Screens.AuthScreen


    NavHost(navController = navController, startDestination = userScreen) {

        composable<Screens.AuthScreen> {
            LoginScreen(
                onSignInSuccess = {navController.navigate(Screens.HomeScreen){
                    popUpTo(Screens.AuthScreen) {
                        inclusive = true
                    }
                } }
            )
        }

        composable<Screens.HomeScreen> {
            HomeScreen()
        }
    }
}