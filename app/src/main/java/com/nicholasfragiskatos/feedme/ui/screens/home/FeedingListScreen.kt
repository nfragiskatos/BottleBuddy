package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem

@Composable
fun FeedingListScreen(
    navController: NavController,
    vm: FeedingListScreenViewModel = hiltViewModel(),
) {
    val feedings = vm.feedings.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(feedings.value) {
            FeedingItem(feeding = it) {
                navController.navigate(NavigationItem.Edit.buildRoute(it.id))
            }
        }
    }
}
