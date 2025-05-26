package com.example.mnfit.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavScreen("home", "Home", Icons.Filled.Home)
    object Terms : BottomNavScreen("terms", "Terms", Icons.Filled.List)
    object Profile : BottomNavScreen("profile", "Profile", Icons.Filled.Person)
}

val bottomNavItems = listOf(
    BottomNavScreen.Home,
    BottomNavScreen.Terms,
    BottomNavScreen.Profile
)
