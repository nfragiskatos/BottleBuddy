package com.nicholasfragiskatos.feedme.ui.screens.edit

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.R
import com.nicholasfragiskatos.feedme.ui.common.UnitSelector
import com.nicholasfragiskatos.feedme.utils.convertUtcToLocalDate
import java.util.Date
import java.util.Locale

// TODO: Add notify contact of updated/new feeding
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    vm: AddEditScreenViewModel = hiltViewModel(),
) {
    val quantity by vm.quantity.collectAsStateWithLifecycle()
    val notes by vm.notes.collectAsStateWithLifecycle()
    val units by vm.units.collectAsStateWithLifecycle()
    val date by vm.date.collectAsStateWithLifecycle()
    val finished = vm.finished.collectAsStateWithLifecycle()

    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    if (finished.value) {
        navController.navigateUp()
    }

    val formattedDate by remember {
        derivedStateOf {
            formatter.format(date)
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(date.time)

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(date.hours, date.minutes, true)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate =
                            convertUtcToLocalDate(datePickerState.selectedDateMillis!!)
                        vm.onEvent(AddEditFeedingEvent.ChangeDate(selectedDate))
                        showDatePicker = false
                    },
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { showTimePicker = false },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Dismiss")
                        }
                        TextButton(
                            onClick = {
                                val updatedDate = Date(date.time).apply {
                                    hours = timePickerState.hour
                                    minutes = timePickerState.minute
                                }
                                vm.onEvent(AddEditFeedingEvent.ChangeDate(updatedDate))
                                showTimePicker = false
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Row {
                        IconButton(onClick = {
                            showDatePicker = true
                        }) {
                            Icon(
                                modifier = Modifier.size(64.dp),
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "calendar",
                            )
                        }

                        IconButton(onClick = {
                            showTimePicker = true
                        }) {
                            Icon(
                                modifier = Modifier.size(64.dp),
                                painter = painterResource(id = R.drawable.time_24),
                                contentDescription = "time",
                            )
                        }
                    }
                },
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = quantity,
                onValueChange = {
                    vm.onEvent(AddEditFeedingEvent.ChangeQuantity(it))
                },
                label = { Text(text = "Volume") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )

            UnitSelector(
                modifier = Modifier.fillMaxWidth(),
                selectedUnit = units
            ) { unit ->
                vm.onEvent(AddEditFeedingEvent.ChangeUnits(unit))
            }

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = notes,
                onValueChange = {
                    vm.onEvent(AddEditFeedingEvent.ChangeNote(it))
                },
                label = { Text(text = "Notes") },
            )

            Button(
                onClick = {
                    vm.saveFeeding()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = quantity.isNotBlank()
            ) {
                val label = if (vm.isAdd) "Add" else "Save"
                Text(text = label)
            }
        }
        if (!vm.isAdd) {
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End),
                onClick = { vm.deleteFeeding() },
            ) {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
