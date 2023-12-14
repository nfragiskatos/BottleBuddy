package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingItem(
    feeding: Feeding,
    onDelete: (Feeding) -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),

        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.clip(shape = RoundedCornerShape(24.dp)).background(color = MaterialTheme.colorScheme.tertiaryContainer).padding(16.dp)
            ) {
                Text(
                    text = feeding.quantity.toString(),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(text = feeding.unit.abbreviation, style = MaterialTheme.typography.titleMedium)
            }
            val formatter = SimpleDateFormat("h:mm a") // SimpleDateFormat.getTimeInstance()
            val date = formatter.format(feeding.date)

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = date, style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { onDelete(feeding) }) {
                    Text(text = "Delete")
                }
//                IconButton(onClick = { /*TODO*/ }) {
//                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete Feeding", tint = MaterialTheme.colorScheme.tertiary )
//                }
            }

        }
    }
}
