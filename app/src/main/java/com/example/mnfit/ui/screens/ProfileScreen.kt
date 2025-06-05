package com.example.mnfit.ui.screens



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mnfit.R
import com.example.mnfit.navigation.Screen
import com.example.mnfit.ui.components.ProfilePicturePicker
import com.example.mnfit.ui.theme.gym_Blue
import com.example.mnfit.ui.theme.gym_LightGray
import com.example.mnfit.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val firstName by userViewModel.firstName.collectAsState()
    val lastName by userViewModel.lastName.collectAsState()
    val context = LocalContext.current
    val photoUrl by userViewModel.photoUrl.collectAsState()
    val role by userViewModel.role.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (currentUser == null) {

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.please_log_in_or_register_to_access_your_profile), style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gym_Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.login))
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
                    Text(stringResource(R.string.register))
                }
            }
        } else {

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProfilePicturePicker(
                    photoUrl=photoUrl,
                    onImageSelected = { uri, bitmap ->
                        userViewModel.uploadProfilePhoto(
                            context = context,
                            imageUri = uri,
                            imageBitmap = bitmap
                        ) { success, messageOrUrl -> }

                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "$firstName $lastName",
                    style = MaterialTheme.typography.titleLarge,
                    color = gym_LightGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = role.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(40.dp))
                if (role == "owner") {
                    Button(
                        onClick = { navController.navigate(Screen.AllUsers.route) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = gym_LightGray, contentColor = gym_Blue)
                    ) {
                        Text(stringResource(R.string.manage_users))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        userViewModel.refreshCurrentUser()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gym_Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.logout))
                }
            }
        }
    }
}
