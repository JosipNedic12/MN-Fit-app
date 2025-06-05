package com.example.mnfit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mnfit.viewmodel.UserViewModel
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.mnfit.R
import com.example.mnfit.ui.theme.gym_Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllUsersScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val users by userViewModel.users.collectAsState()
    val roles = listOf("user", "trainer", "owner")
    var expandedUserId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredUsers = users.filter { user ->
        val fullName = "${user.firstName} ${user.lastName}".lowercase()
        searchQuery.lowercase() in fullName
    }
    LaunchedEffect(Unit) {
        userViewModel.listenForAllUsers()
    }


    Column{
        // Top bar
        TopAppBar(
            colors =  TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.2f)),
            title = { Text(stringResource(R.string.manage_users)) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.go_back))
                }
            }
        )
        OutlinedTextField(
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = gym_Blue,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
                ),
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(R.string.search_by_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(filteredUsers) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            expandedUserId = if (expandedUserId == user.uid) null else user.uid
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = gym_Blue.copy(alpha = 0.6f) // semi-transparent blue
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${user.firstName} ${user.lastName}", color = Color.White)
                            Text(user.email, style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                        if (expandedUserId == user.uid) {
                            var selectedRole by remember { mutableStateOf(user.role) }
                            DropdownMenu(
                                expanded = true,
                                onDismissRequest = { expandedUserId = null }
                            ) {
                                roles.forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role.replaceFirstChar { it.uppercase() }) },
                                        onClick = {
                                            selectedRole = role
                                            userViewModel.updateUserRole(user.uid, role)
                                            expandedUserId = null
                                        }
                                    )
                                }
                            }
                        } else {
                            Text(user.role.replaceFirstChar { it.uppercase() }, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
