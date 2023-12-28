package com.nicholasfragiskatos.feedme.ui.screens.home.draggable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FeedingDeleteAction(modifier: Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.error)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Feeding Item",
                tint = MaterialTheme.colorScheme.onError
            )

            Text(
                text = "Delete",
                color = MaterialTheme.colorScheme.onError,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}