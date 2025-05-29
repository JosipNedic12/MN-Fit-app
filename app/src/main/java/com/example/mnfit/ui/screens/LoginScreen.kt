import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mnfit.R
import com.example.mnfit.viewmodel.AuthViewModel
import com.example.mnfit.viewmodel.AuthState
import com.example.mnfit.navigation.Screen
import com.example.mnfit.ui.theme.gym_Blue
import com.example.mnfit.ui.theme.gym_LightGray

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state = authViewModel.authState

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp) // Adjust size as needed
            )
            Text("Login", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { authViewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = gym_Blue,
                    contentColor = Color.White
                )
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Don't have an account? Register",
                    color = gym_LightGray)
            }
            when (state) {
                is AuthState.Loading -> CircularProgressIndicator()
                is AuthState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is AuthState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) // This clears the back stack
                        }
                        authViewModel.resetState() // Optional: reset state after navigation
                    }
                }
                else -> {}
            }
        }
    }
}
