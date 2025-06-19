package com.example.mnfit

import LoginScreen
import MainBottomNavBar
import RegisterScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mnfit.navigation.BottomNavScreen
import com.example.mnfit.navigation.Screen
import com.example.mnfit.navigation.bottomNavItems
import com.example.mnfit.ui.screens.HomeScreen
import com.example.mnfit.ui.screens.ProfileScreen
import com.example.mnfit.ui.theme.MNFItTheme
import com.example.mnfit.model.Term
import com.example.mnfit.ui.screens.AllUsersScreen
import com.example.mnfit.ui.screens.TermsScreen
import com.example.mnfit.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

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
fun MainApp(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    // Keep track of whether we've already navigated to the correct start screen
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        if (!hasNavigated) {
            if (isLoggedIn) {
                navController.navigate(BottomNavScreen.Home.route) {
                    popUpTo(0)
                }
            } else {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
            hasNavigated = true
        }
    }

    Scaffold(
        bottomBar = { MainBottomNavBar(navController, bottomNavItems) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image fills the whole screen
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            // NavHost content above the background, respects bottom bar
            NavHost(
                navController = navController,
                startDestination = BottomNavScreen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavScreen.Home.route) { HomeScreen(navController) }
                composable(BottomNavScreen.Terms.route) { TermsScreen(navController) }
                composable(BottomNavScreen.Profile.route) { ProfileScreen(navController) }
                composable(Screen.Login.route) { LoginScreen(navController) }
                composable(Screen.Register.route) { RegisterScreen(navController) }
                composable(Screen.AllUsers.route) { AllUsersScreen(navController)}
            }
        }
    }
}


