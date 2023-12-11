package com.nicholasfragiskatos.feedme.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun HomeScreen() {
    Text(text = stringResource(id = NavigationItem.Home.resourceId))
}
