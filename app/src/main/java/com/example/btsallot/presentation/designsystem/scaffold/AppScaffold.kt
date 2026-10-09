package com.example.btsallot.presentation.designsystem.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun AppScaffold(
    title: String? = null,
    onBackClick: (()-> Unit)? = null,
    currentScreen: BottomNavItem? = null,
    onNavigate: ((BottomNavItem) -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
){
    Scaffold(
        topBar = {BTSTopBar(
            title = title,
            onBackClick = onBackClick
        ) },
        bottomBar = {
            if (currentScreen!= null && onNavigate != null) {
                BTSBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = onNavigate
                )
            }

        },
        content = content
    )
}