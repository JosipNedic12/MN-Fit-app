import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.mnfit.viewmodel.FCMState

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val authState by authViewModel.authState.collectAsState()
    val fcmState by authViewModel.fcmState.collectAsState()
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
            Text(stringResource(R.string.login), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
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
                Text(stringResource(R.string.login))
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    stringResource(R.string.don_t_have_an_account_register),
                    color = gym_LightGray)
            }

            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator()
                }
                is AuthState.Error -> {
                    LaunchedEffect(authState) {
                        val message = (authState as AuthState.Error).message
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is AuthState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0)
                        }
                    }
                }
                else -> {}
            }

            when (fcmState) {
                is FCMState.Error -> {
                    LaunchedEffect(fcmState) {
                        val message = (fcmState as FCMState.Error).message
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        }
    }
}
