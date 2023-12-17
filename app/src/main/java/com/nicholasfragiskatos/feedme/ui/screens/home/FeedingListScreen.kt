package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedingListScreen(
    navController: NavController,
    displayUnits: UnitOfMeasurement,
    vm: FeedingListScreenViewModel = hiltViewModel(),
) {
    val grouping = vm.grouping.collectAsState()

    val dateTimeFormatter = remember {
        DateTimeFormatter.ofPattern("EEE, LLL dd, YYYY")
    }

    Column {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            grouping.value.forEach { (date, feedings) ->
                
                val dayTotal = feedings.map { feeding -> UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, displayUnits) }.sum()

                stickyHeader {
                    val format1 = date.format(dateTimeFormatter)

                    Row (modifier = Modifier.fillMaxWidth()
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

                        Text(text = "Total: %.2f".format(dayTotal),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )


                    }

                }

                itemsIndexed(feedings) { index, feeding ->
                    FeedingItem(feeding = feeding, displayUnits, onDelete = {
                        vm.deleteFeeding(feeding)
                    }) {
                        navController.navigate(NavigationItem.Edit.buildRoute(feeding.id))
                    }
                }
            }
        }
    }
}
