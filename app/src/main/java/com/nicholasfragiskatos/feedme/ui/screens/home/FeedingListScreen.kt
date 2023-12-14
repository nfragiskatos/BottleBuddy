package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedingListScreen(
    navController: NavController,
    vm: FeedingListScreenViewModel = hiltViewModel(),
) {
    val feedings = vm.feedings.collectAsState()
    val grouping = vm.grouping.collectAsState()

//    LazyColumn(modifier = Modifier.fillMaxSize()) {
//
//        items(feedings.value) {
//            FeedingItem(feeding = it) {
//                navController.navigate(NavigationItem.Edit.buildRoute(it.id))
//            }
//        }
//    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        grouping.value.forEach { (date, feedings) ->
            stickyHeader {

//val formatter =                SimpleDateFormat("EEE, LLL dd, YYYY")
//                val format = formatter.format(date)
                val ofPattern = DateTimeFormatter.ofPattern("EEE, LLL dd, YYYY")
                val format1 = date.format(ofPattern)

                Text(
                    text = format1,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.tertiaryContainer).padding(4.dp),
                )
            }

            itemsIndexed(feedings) { index, feeding ->
                FeedingItem(feeding = feeding) {
                    navController.navigate(NavigationItem.Edit.buildRoute(feeding.id))
                }

                if (index != feedings.lastIndex) {
                    Divider()
                }
            }
        }
    }
}
