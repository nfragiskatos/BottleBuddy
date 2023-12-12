package com.nicholasfragiskatos.feedme.ui.screens.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.domain.UnitOfMeasurement

@Composable
fun EditScreen(
    navController: NavController,
    vm: AddEditScreenViewModel = hiltViewModel(),
) {

    val quantity = vm.quantity.collectAsState()
    val notes = vm.notes.collectAsState()
    val units = vm.units.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = quantity.value,
            onValueChange = {
                vm.onEvent(AddEditFeedingEvent.ChangeQuantity(it))
            },
            label = { Text(text = "Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
        )

        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            UnitOfMeasurement.entries.forEach { unit ->
                Row(
                    modifier = Modifier.selectableGroup()
                        .selectable(
                            selected = (units.value == unit),
                            onClick = {
                                vm.onEvent(AddEditFeedingEvent.ChangeUnits(unit))
                            },
                        )
                        .padding(16.dp),
                ) {
                    RadioButton(selected = (units.value == unit), onClick = null)
                    Text(
                        text = unit.abbreviation,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            }
        }

        TextField(value = notes.value, onValueChange = {
            vm.onEvent(AddEditFeedingEvent.ChangeNote(it))
        }, label = { Text(text = "Notes") })
        Button(onClick = {
            vm.saveFeeding()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Add")
        }
    }
}
