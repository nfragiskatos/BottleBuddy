package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import java.text.SimpleDateFormat

@Composable
fun FeedingItem(
    feeding: Feeding,
    displayUnit: UnitOfMeasurement,
    onDelete: (Feeding) -> Unit,
    onClick: () -> Unit,
) {

    val formatter = remember {
        SimpleDateFormat("h:mm a")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
    ) {
        Surface(tonalElevation = 8.dp) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "%.2f".format(
                            UnitUtils.convertMeasurement(
                                feeding.quantity,
                                feeding.unit,
                                displayUnit
                            )
                        ),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = displayUnit.abbreviation,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                val date = formatter.format(feeding.date)

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = date, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { onDelete(feeding) }) {
                        Text(text = "Delete", style = MaterialTheme.typography.labelLarge)
                    }
                }

            }
        }
    }
}
