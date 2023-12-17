package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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

    var expanded by remember {
        mutableStateOf(false)
    }

    var isExpandable by remember {
        mutableStateOf(false)
    }

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxSize()

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
                        if (isExpandable || expanded) {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (expanded) "hide notes" else "show notes"
                                )
                            }
                        }
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
                if (feeding.notes.isNotBlank()) {
                    Divider()
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = feeding.notes,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                        onTextLayout = { result ->
                            isExpandable = result.hasVisualOverflow
                        }
                    )
                }
            }
        }
    }
}
