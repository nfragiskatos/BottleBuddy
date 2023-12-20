package com.nicholasfragiskatos.feedme.ui.screens.home

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
import androidx.compose.material3.Button
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedingListScreen(
    navController: NavController,
    displayUnits: UnitOfMeasurement,
    vm: FeedingListScreenViewModel = hiltViewModel(),
) {
    val grouping by vm.grouping.collectAsState()
    val loading by vm.loading.collectAsState()
    val graphPoints by vm.graphPoints.collectAsState()
    val goal by vm.dailyGoal.collectAsState()

    val dateTimeFormatter = remember {
        DateTimeFormatter.ofPattern("EEE, LLL dd, YYYY")
    }

    Column {
        CumulativeGoalGraph(pointsData = graphPoints, goal)

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
                            displayUnits
                        )
                    }

                    stickyHeader {
                        val format1 = date.format(dateTimeFormatter)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.surface)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = format1,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.secondary,
                            )

                            Text(
                                text = "Total: %.2f".format(dayTotal),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
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
                                FeedingItem(feeding = feeding, displayUnits) {
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
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "No bottles have been counted yet.")
        Button(onClick = { navController.navigate(
            NavigationItem.Edit.buildRoute(0L)
        ) }) {
            Text(text = "Add your first bottle now!")
        }
    }
}
