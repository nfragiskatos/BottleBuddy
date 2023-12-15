package com.nicholasfragiskatos.feedme.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement

@Composable
fun UnitSelector(
    modifier: Modifier = Modifier,
    selectedUnit: UnitOfMeasurement,
    onSelect: (UnitOfMeasurement) -> Unit
) {
    Row(
        modifier = modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
        UnitOfMeasurement.entries.forEach { unit ->
            Row(
                modifier = Modifier
                    .selectableGroup()
                    .selectable(
                        selected = (selectedUnit == unit),
                        onClick = {
                            onSelect(unit)
                        },
                    ),
            ) {
                RadioButton(selected = (selectedUnit == unit), onClick = null)
                Text(
                    text = unit.abbreviation,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}