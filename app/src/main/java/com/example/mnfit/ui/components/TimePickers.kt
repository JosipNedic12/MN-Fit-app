package com.example.mnfit.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.mnfit.R
import com.example.mnfit.ui.theme.gym_Blue
import com.example.mnfit.ui.theme.gym_LightBlue
import com.example.mnfit.ui.theme.gym_LightGray


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun MyTimePickerDialog(
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        is24Hour = true,
    )
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_time)) },
        confirmButton = {
            Button(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismiss()
            },
                colors = ButtonDefaults.buttonColors(contentColor = gym_LightGray, containerColor = gym_LightBlue)) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = gym_Blue,
                    selectorColor = gym_LightGray,
                    containerColor = gym_Blue,
                    periodSelectorBorderColor = gym_LightGray,
                    clockDialSelectedContentColor = Color.Black,
                    clockDialUnselectedContentColor = Color.LightGray,
                    periodSelectorSelectedContainerColor = gym_LightGray,
                    periodSelectorUnselectedContainerColor = gym_Blue,
                    periodSelectorSelectedContentColor = Color.Black,
                    periodSelectorUnselectedContentColor = Color.White,
                    timeSelectorSelectedContainerColor = gym_LightGray,
                    timeSelectorUnselectedContainerColor = gym_Blue,
                    timeSelectorSelectedContentColor = Color.Black,
                    timeSelectorUnselectedContentColor = Color.White
                )
            )
        }
    )
}
