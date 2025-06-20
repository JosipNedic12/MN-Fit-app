package com.example.mnfit.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object AllUsers: Screen("all_users_screen")
}
