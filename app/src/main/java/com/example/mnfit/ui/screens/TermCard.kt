package com.example.mnfit.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mnfit.model.Term
import com.example.mnfit.ui.theme.gym_Blue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TermCard(
    term: Term,
    isUserSignedUp: Boolean,
    onClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d, HH:mm", Locale.getDefault()) }
    var showSignUpDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = gym_Blue.copy(alpha = 0.50f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(term.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(term.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Location: ${term.location}", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Date: ${dateFormat.format(Date(term.date))}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Max participants: ${term.maxParticipants}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!isUserSignedUp) {
                Button(
                    onClick = { showSignUpDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gym_Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Sign Up")
                }
            } else {
                Button(
                    onClick = { showSignOutDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.8f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Sign Out")
                }
            }
        }
    }

    // Sign Up Confirmation Dialog
    if (showSignUpDialog) {
        AlertDialog(
            onDismissRequest = { showSignUpDialog = false },
            title = { Text("Confirm Sign Up") },
            text = {
                Column {
                    Text("Are you sure you want to sign up for term?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(dateFormat.format(Date(term.date)), style = MaterialTheme.typography.titleMedium)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignUpDialog = false
                        onSignUpClick()
                    }
                ) {
                    Text("Yes, Sign Up")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignUpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Sign Out Confirmation Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Confirm Sign Out") },
            text = {
                Column {
                    Text("Are you sure you want to leave this term?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(dateFormat.format(Date(term.date)), style = MaterialTheme.typography.titleMedium)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        onSignOutClick()
                    }
                ) {
                    Text("Yes, Leave")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}