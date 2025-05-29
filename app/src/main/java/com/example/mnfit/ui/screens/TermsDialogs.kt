import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mnfit.R
import com.example.mnfit.model.Term
import com.example.mnfit.ui.components.MyDatePickerDialog
import com.example.mnfit.ui.components.MyTimePickerDialog
import com.example.mnfit.ui.theme.gym_Blue
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

@Composable
fun AddTermDialog(
    onDismiss: () -> Unit,
    onAddTerm: (Term) -> Unit,
    trainerId: String
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var maxParticipants by remember { mutableStateOf("") }

    // Date and time state
    var dateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    var showTimePicker by remember { mutableStateOf(false) }
    var hour by remember { mutableStateOf<Int?>(null) }
    var minute by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Term") },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = location, onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = maxParticipants, onValueChange = { maxParticipants = it },
                    label = { Text("Max Participants") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date Picker Button
                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gym_Blue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (dateMillis == null)
                            "Select Date"
                        else
                            java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(java.util.Date(dateMillis!!))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time Picker Button
                Button(
                    onClick = { showTimePicker = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gym_Blue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (hour == null || minute == null)
                            "Select Time"
                        else
                            String.format("%02d:%02d", hour, minute)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Combine dateMillis and hour/minute into a single timestamp
                    val cal = java.util.Calendar.getInstance()
                    if (dateMillis != null) cal.timeInMillis = dateMillis!!
                    cal.set(java.util.Calendar.HOUR_OF_DAY, hour ?: 0)
                    cal.set(java.util.Calendar.MINUTE, minute ?: 0)
                    cal.set(java.util.Calendar.SECOND, 0)
                    cal.set(java.util.Calendar.MILLISECOND, 0)
                    val term = Term(
                        termId = java.util.UUID.randomUUID().toString(),
                        title = title,
                        description = description,
                        trainerId = trainerId,
                        date = cal.timeInMillis,
                        location = location,
                        maxParticipants = maxParticipants.toIntOrNull() ?: 0
                    )
                    onAddTerm(term)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = gym_Blue,
                    contentColor = Color.White
                ),
                enabled = title.isNotBlank() && dateMillis != null && hour != null && minute != null
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0f),
                    contentColor = Color.White
                )
            ) { Text("Cancel") }
        }
    )

    // Show the Compose DatePicker dialog
    if (showDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { millis ->
                dateMillis = millis
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Show the Compose TimePicker dialog
    if (showTimePicker) {
        MyTimePickerDialog(
            onTimeSelected = { h, m ->
                hour = h
                minute = m
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}


@Composable
fun TermDetailsDialog(
    term: Term,
    currentUserUid: String?,
    participantNames: List<String>,
    onDismiss: () -> Unit,
    onSignUp: (Term) -> Unit,
    signUpMsg: String?
) {
    val dateFormat = remember { SimpleDateFormat("EEE, HH:mm", Locale.getDefault()) }
    val fullDateFormat = remember { SimpleDateFormat("EEE, MMM d, HH:mm", Locale.getDefault()) }
    val isUserSignedUp = term.participants.contains(currentUserUid)
    val isFull = term.participants.size >= term.maxParticipants

    var showConfirmDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(term.title) },
        text = {
            Column {
                Text(term.description)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Location: ${term.location}")
                Text("Date: ${fullDateFormat.format(Date(term.date))}")
                Text("Max participants: ${term.maxParticipants}")
                Spacer(modifier = Modifier.height(12.dp))
                Text("Participants:", style = MaterialTheme.typography.titleMedium)
                if (participantNames.isEmpty()) {
                    Text("No one signed up yet.")
                } else {
                    participantNames.forEach { name ->
                        Text("• $name")
                    }
                }
                signUpMsg?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            Row {
                if (!isUserSignedUp && !isFull && currentUserUid != null) {
                    Button(
                        onClick = { showConfirmDialog = true }
                    ) {
                        Text("Sign Up")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        }
    )

    // Confirmation dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
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
                        showConfirmDialog = false
                        onSignUp(term)
                    }
                ) { Text("Yes, Sign Up") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}