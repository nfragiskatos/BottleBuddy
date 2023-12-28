package com.nicholasfragiskatos.feedme.ui.screens.home

import android.content.Intent
import android.text.format.DateFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem
import com.nicholasfragiskatos.feedme.utils.DateUtils
import com.nicholasfragiskatos.feedme.utils.UnitUtils

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedingListScreen(
    navController: NavController,
    vm: FeedingListScreenViewModel = hiltViewModel(),
) {
    val grouping by vm.grouping.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val preferences by vm.preferences.collectAsStateWithLifecycle()
    var isProgressExpanded by remember { mutableStateOf(true) }
    val graphPoints by vm.graphPoints.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { isProgressExpanded = !isProgressExpanded }) {
                Text(text = "${if (isProgressExpanded) "Hide" else "Show"} Today's Progress")
            }
        }
        if (isProgressExpanded) {
            CumulativeGoalGraph(
                pointsData = graphPoints,
                preferences = preferences,
            )
        }
        if (grouping.isEmpty() && !loading) {
            MyEmptyListView(navController)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyListState()
            ) {
                grouping.forEach { (date, feedings) ->

                    val dayTotal = feedings.sumOf { feeding ->
                        UnitUtils.convertMeasurement(
                            feeding.quantity,
                            feeding.unit,
                            preferences.displayUnit
                        )
                    }

                    stickyHeader {
                        val formattedDate = DateUtils.getFormattedDate(date)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                )

                                // TODO: Fix mL formatting, probably don't care about decimals. Should create a formatter class
                                Text(
                                    text = "Total: %.2f${preferences.displayUnit.abbreviation}".format(
                                        dayTotal
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {

                                    val is24HourFormat = DateFormat.is24HourFormat(context)
                                    val total = "%.2f".format(dayTotal)
                                    val sb = StringBuilder("Summary for ${DateUtils.getFormattedDate(date)}\n")
                                    for (index in feedings.indices.reversed()) {
                                        val feeding = feedings[index]
                                        val quantity = "%.2f".format(UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, preferences.displayUnit))
                                        sb.append("\n${DateUtils.getFormattedTime(feeding.date, is24HourFormat)} - $quantity${preferences.displayUnit.abbreviation}")
                                    }
                                    sb.append("\n------------")
                                    sb.append("\nTotal: $total${preferences.displayUnit.abbreviation}")

                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            sb.toString()
                                        )
                                    }
                                    val chooser = Intent.createChooser(intent, "Send Feeding via")
                                    context.startActivity(chooser)
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = "Share Daily Summary"
                                    )
                                }
                            }
                        }
                    }

                    items(
                        items = feedings,
                        key = { it.id }
                    ) { feeding ->
                        val state = rememberDismissState(
                            confirmValueChange = {
                                if (it == DismissValue.DismissedToEnd) {
                                    vm.deleteFeeding(feeding)
                                    true
                                } else
                                    false
                            }
                        )

                        SwipeToDismiss(
                            directions = setOf(DismissDirection.StartToEnd),
                            state = state,
                            background = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.error)
                                        .padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Feeding Item",
                                        tint = MaterialTheme.colorScheme.onError,
                                        modifier = Modifier.align(
                                            Alignment.CenterStart
                                        )
                                    )
                                }
                            }, dismissContent = {
                                FeedingItem(feeding = feeding, preferences.displayUnit) {
                                    navController.navigate(NavigationItem.Edit.buildRoute(feeding.id))
                                }
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun MyEmptyListView(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "No bottles have been counted yet.")
        Button(onClick = {
            navController.navigate(
                NavigationItem.Edit.buildRoute(0L)
            )
        }) {
            Text(text = "Add your first bottle now!")
        }
    }
}
