package com.nicholasfragiskatos.feedme.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.ui.common.UnitSelector


@Composable
fun SettingsDialog(
    goal: String,
    unitOfMeasurement: UnitOfMeasurement,
    preferredUnits: UnitOfMeasurement,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, UnitOfMeasurement, UnitOfMeasurement) -> Unit
) {

    var text by remember { mutableStateOf(goal) }

    var unit by remember { mutableStateOf(unitOfMeasurement) }

    var prefUnits by remember { mutableStateOf(preferredUnits) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    Text(text = "Daily Goal", style = MaterialTheme.typography.headlineLarge)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    OutlinedTextField(
                        label = { Text(text = "Total") },
                        modifier = Modifier.fillMaxWidth(),
                        value = text,
                        onValueChange = {
                            text = it
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                    UnitSelector(
                        modifier = Modifier.fillMaxWidth(),
                        selectedUnit = unit,
                        alignment = Alignment.Start
                    ) {
                        unit = it
                    }
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp), contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "Preferred Units", style = MaterialTheme.typography.headlineLarge)
                }

                UnitSelector(
                    modifier = Modifier.fillMaxWidth(),
                    selectedUnit = prefUnits,
                    alignment = Alignment.Start
                ) {
                    prefUnits = it
                }

                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = "Dismiss")
                    }

                    TextButton(onClick = { onConfirmation(text, unit, prefUnits) }) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }
}