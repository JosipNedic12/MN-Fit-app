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
import com.example.mnfit.ui.theme.gym_LightBlue
import com.example.mnfit.ui.theme.gym_LightGray

@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("subscriber") }
    var roleMenuExpanded by remember { mutableStateOf(false) }
    val state = authViewModel.authState

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Foreground UI
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Register", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { roleMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(role.replaceFirstChar { it.uppercase() }, color = gym_LightGray)
                }
                DropdownMenu(
                    expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false }
                ) {
                    listOf("owner", "trainer", "subscriber").forEach {
                        DropdownMenuItem(
                            text = { Text(it.replaceFirstChar { c -> c.uppercase() },
                                color = gym_Blue)},
                            onClick = {
                                role = it
                                roleMenuExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { authViewModel.register(email, password, firstName, lastName, role) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = gym_Blue,
                    contentColor = Color.White
                )
            ) {
                Text("Register")
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = { navController.navigate(Screen.Login.route) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Already have an account? Login",
                    color = gym_LightGray)
            }
            when (state) {
                is AuthState.Loading -> CircularProgressIndicator()
                is AuthState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is AuthState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
