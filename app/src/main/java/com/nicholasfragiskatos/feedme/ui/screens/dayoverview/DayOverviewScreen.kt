package com.nicholasfragiskatos.feedme.ui.screens.dayoverview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun DayOverviewScreen(
    navController: NavController,
    vm: DayOverviewScreenViewModel = hiltViewModel()
) {

    Text(text = "HELLO FROM DAY OVERVIEW SCREEN")
}