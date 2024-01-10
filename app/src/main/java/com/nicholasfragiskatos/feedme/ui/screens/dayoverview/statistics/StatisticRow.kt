package com.nicholasfragiskatos.feedme.ui.screens.dayoverview.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun StatisticRow(
    stat: String,
    value: String,
    unit: String,
    displayUnit: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stat, style = MaterialTheme.typography.titleLarge)
        val display = if (displayUnit) "$value$unit" else value
        Text(
            text = display,
            style = MaterialTheme.typography.titleMedium
        )
    }
}