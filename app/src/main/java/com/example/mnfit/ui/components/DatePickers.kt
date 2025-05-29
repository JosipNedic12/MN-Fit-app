package com.example.mnfit.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.mnfit.ui.theme.gym_Blue
import com.example.mnfit.ui.theme.gym_Coral
import com.example.mnfit.ui.theme.gym_LightBlue
import com.example.mnfit.ui.theme.gym_LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onDateSelected(datePickerState.selectedDateMillis) },
                colors = ButtonDefaults.buttonColors(contentColor = gym_LightGray, containerColor = gym_LightBlue)) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(contentColor = gym_LightGray, containerColor = Color.Transparent)
                ) { Text("Cancel") }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = gym_Blue,
                titleContentColor = Color.White,
                headlineContentColor = Color.White,
                weekdayContentColor = Color.White,
                subheadContentColor = Color.White,
                navigationContentColor = Color.White,
                yearContentColor = Color.White,
                disabledYearContentColor = Color.LightGray,
                currentYearContentColor = gym_LightGray,
                selectedYearContentColor = gym_LightGray,
                disabledSelectedYearContentColor = Color.LightGray,
                selectedYearContainerColor = gym_Blue,
                disabledSelectedYearContainerColor = Color.LightGray,
                dayContentColor = Color.White,
                disabledDayContentColor = Color.LightGray,
                selectedDayContentColor = Color.Black,
                disabledSelectedDayContentColor = Color.LightGray,
                selectedDayContainerColor = gym_LightGray,
                disabledSelectedDayContainerColor = Color.LightGray,
                todayContentColor = gym_LightGray,
                todayDateBorderColor = gym_LightGray,
                dayInSelectionRangeContainerColor = gym_LightBlue,
                dayInSelectionRangeContentColor = Color.White,
                dividerColor = gym_LightGray,
                dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = gym_Blue,
                    focusedLabelColor = gym_LightGray,
                    unfocusedLabelColor = gym_Blue
                )
            )
        )
    }
}
