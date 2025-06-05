package com.example.mnfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mnfit.R
import com.example.mnfit.model.Term
import com.example.mnfit.ui.components.MyDatePickerDialog
import com.example.mnfit.ui.components.MyTimePickerDialog
import com.example.mnfit.ui.theme.gym_Blue
import java.text.SimpleDateFormat
import java.util.*


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
    var dateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var hour by remember { mutableStateOf<Int?>(null) }
    var minute by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.create_new_term)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text(stringResource(R.string.title)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = location, onValueChange = { location = it },
                    label = { Text(stringResource(R.string.locationTerm)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = maxParticipants, onValueChange = { maxParticipants = it },
                    label = { Text(stringResource(R.string.max_participantsTerm)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                            stringResource(R.string.select_date)
                        else
                            java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(java.util.Date(dateMillis!!))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                            stringResource(R.string.select_time)
                        else
                            String.format("%02d:%02d", hour, minute)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
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
            ) { Text(stringResource(R.string.add)) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0f),
                    contentColor = Color.White
                )
            ) { Text(stringResource(R.string.cancel)) }
        }
    )
    if (showDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { millis ->
                dateMillis = millis
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
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

enum class ConfirmAction { SIGN_UP, DELETE }

@Composable
fun TermDetailsDialog(
    term: Term,
    currentUserUid: String?,
    participantNames: List<String>,
    isTrainer: Boolean,
    onRemoveTerm: (Term) -> Unit,
    onDismiss: () -> Unit,
    onSignUp: (Term) -> Unit,
    signUpMsg: String?
) {
    val dateFormat = remember { SimpleDateFormat("EEE, HH:mm", Locale.getDefault()) }
    val isUserSignedUp = term.participants.contains(currentUserUid)
    val isFull = term.participants.size >= term.maxParticipants

    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmAction by remember { mutableStateOf(ConfirmAction.SIGN_UP) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(term.title, modifier = Modifier.align(Alignment.CenterStart))
                if (isTrainer) {
                    IconButton(
                        onClick = {
                            confirmAction = ConfirmAction.DELETE
                            showConfirmDialog = true
                        },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.remove_term),
                            tint = Color.Red
                        )
                    }
                }
            }
        },
        text = {
            Column {
                Text(term.description)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.location,term.location))
                Text(stringResource(R.string.date,term.date))
                Text(stringResource(R.string.max_participants,term.maxParticipants))
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.participants), style = MaterialTheme.typography.titleMedium)
                if (participantNames.isEmpty()) {
                    Text(stringResource(R.string.no_one_signed_up_yet))
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
                        onClick = {
                            confirmAction = ConfirmAction.SIGN_UP
                            showConfirmDialog = true
                        }
                    ) {
                        Text(stringResource(R.string.sign_up))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) }
            }
        }
    )

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    when (confirmAction) {
                        ConfirmAction.SIGN_UP -> stringResource(R.string.confirm_sign_up)
                        ConfirmAction.DELETE -> stringResource(R.string.confirm_delete)
                    }
                )
            },
            text = {
                Column {
                    Text(
                        when (confirmAction) {
                            ConfirmAction.SIGN_UP -> stringResource(R.string.are_you_sure_you_want_to_sign_up_for_term)
                            ConfirmAction.DELETE -> stringResource(R.string.are_you_sure_you_want_to_delete_this_term)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(dateFormat.format(Date(term.date)), style = MaterialTheme.typography.titleMedium)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        when (confirmAction) {
                            ConfirmAction.SIGN_UP -> onSignUp(term)
                            ConfirmAction.DELETE -> {
                                onRemoveTerm(term)
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Text(
                        when (confirmAction) {
                            ConfirmAction.SIGN_UP -> stringResource(R.string.yes_sign_up)
                            ConfirmAction.DELETE -> stringResource(R.string.delete)
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
