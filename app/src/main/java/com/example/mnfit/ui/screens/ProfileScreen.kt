package com.example.mnfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mnfit.navigation.Screen
import com.example.mnfit.ui.theme.gym_Blue
import com.example.mnfit.ui.theme.gym_LightGray
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController) {
    // Use a state to trigger recomposition after logout
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (currentUser == null) {
            // Not logged in: show login and register buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Please log in or register to access your profile.", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gym_Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Register.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.65f),
                        contentColor = gym_LightGray
                    )
                ) {
                    Text("Register")
                }
            }
        } else {
            // Logged in: show welcome message and logout button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to your profile page!",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        currentUser = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gym_Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Logout")
                }
            }
        }
    }
}
