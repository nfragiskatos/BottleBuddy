package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nicholasfragiskatos.feedme.domain.model.Feeding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingItem(
    feeding: Feeding,
    onClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column {
            Text(text = feeding.id.toString())
            Text(text = feeding.date.toString())
        }
    }
}
