package com.nicholasfragiskatos.feedme.ui.screens.edit

import android.content.Intent
import android.text.format.DateFormat
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.R
import com.nicholasfragiskatos.feedme.ui.common.FeedingDatePickerDialog
import com.nicholasfragiskatos.feedme.ui.common.FeedingTimePickerDialog
import com.nicholasfragiskatos.feedme.ui.common.UnitSelector
import com.nicholasfragiskatos.feedme.utils.dates.DateFormatter

@Composable
fun EditScreen(
    navController: NavController,
    vm: AddEditScreenViewModel = hiltViewModel(),
) {
    val quantity by vm.quantity.collectAsStateWithLifecycle()
    val notes by vm.notes.collectAsStateWithLifecycle()
    val units by vm.units.collectAsStateWithLifecycle()
    val date by vm.date.collectAsStateWithLifecycle()
    val preferences by vm.preferences.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var sendFeeding by remember { mutableStateOf(true) }

    Log.d("MY_TAG", "composed!")

    val formattedDate by remember {
        derivedStateOf {
            DateFormatter.getFormattedDateWithTime(date, DateFormat.is24HourFormat(context))
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        FeedingDatePickerDialog(date = date, onDismiss = { showDatePicker = false }) {
            vm.onEvent(AddEditFeedingEvent.ChangeDate(it))
            showDatePicker = false
        }
    }

    if (showTimePicker) {
        FeedingTimePickerDialog(date = date, onDismiss = { showTimePicker = false }) {
            vm.onEvent(AddEditFeedingEvent.ChangeDate(it))
            showTimePicker = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                onValueChange = remember(vm) {
                    {
                        vm.onEvent(AddEditFeedingEvent.ChangeQuantity(it))
                    }
                },
                label = { Text(text = "Volume") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
            )

            UnitSelector(
                modifier = Modifier.fillMaxWidth(),
                selectedUnit = units,
                onSelect = remember(vm) {
                    { unit ->
                        vm.onEvent(AddEditFeedingEvent.ChangeUnits(unit))
                    }
                },
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = notes,
                onValueChange = remember(vm) {
                    {
                        vm.onEvent(AddEditFeedingEvent.ChangeNote(it))
                    }
                },
                label = { Text(text = "Notes") },
            )

            if (vm.isAdd) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "Share Feeding",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Share this feeding with your contacts after saving.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Switch(
                        modifier = Modifier.padding(start = 8.dp),
                        checked = sendFeeding,
                        onCheckedChange = { sendFeeding = it },
                        thumbContent = if (sendFeeding) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Send Feeding Checkbox",
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }

            Button(
                onClick = remember(vm) {
                    {
                        vm.saveFeeding(
                            generateSummary = sendFeeding,
                            is24HourFormat = DateFormat.is24HourFormat(context),
                            displayUnit = preferences.displayUnit,
                        ) { summary ->
                            if (summary != null && vm.isAdd) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, summary)
                                }
                                val chooser = Intent.createChooser(intent, "Send Feeding via")
                                context.startActivity(chooser)
                            }
                            navController.navigateUp()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = quantity.isNotBlank(),
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
                onClick = remember(vm) {
                    {
                        vm.deleteFeeding {
                            navController.navigateUp()
                        }
                    }
                },
            ) {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
