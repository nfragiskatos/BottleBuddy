package com.nicholasfragiskatos.feedme.ui.common

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.nicholasfragiskatos.feedme.utils.DateUtils
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingDatePickerDialog(
    date: Date,
    onDismiss: () -> Unit,
    onConfirm: (Date) -> Unit
) {
    val datePickerState = rememberDatePickerState(date.time)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDate =
                        DateUtils.convertUtcToLocalDate(datePickerState.selectedDateMillis!!)
                            .apply {
                                hours = date.hours
                                minutes = date.minutes
                                seconds = date.seconds
                            }
                    onConfirm(selectedDate)
                },
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}