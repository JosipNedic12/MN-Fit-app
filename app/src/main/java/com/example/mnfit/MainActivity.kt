package com.example.mnfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mnfit.navigation.BottomNavScreen
import com.example.mnfit.navigation.Screen
import com.example.mnfit.navigation.bottomNavItems
import com.example.mnfit.ui.components.MainBottomNavBar
import com.example.mnfit.ui.screens.HomeScreen
import com.example.mnfit.ui.screens.LoginScreen
import com.example.mnfit.ui.screens.ProfileScreen
import com.example.mnfit.ui.screens.RegisterScreen
import com.example.mnfit.ui.screens.TermsScreen
import com.example.mnfit.ui.theme.MNFItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MNFItTheme {
                MainApp()
            }
        }
    }
}



@Composable
fun MainApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { MainBottomNavBar(navController, bottomNavItems) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Home.route) { HomeScreen(navController) }
            composable(BottomNavScreen.Terms.route) { TermsScreen(navController) }
            composable(BottomNavScreen.Profile.route) { ProfileScreen(navController) }
        }
    }
}
