package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.ui.screens.NavigationItem

@Composable
fun EmptyFeedingListView(navController: NavController) {
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