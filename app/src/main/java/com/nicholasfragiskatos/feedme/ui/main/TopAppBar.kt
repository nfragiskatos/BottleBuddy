package com.nicholasfragiskatos.feedme.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.nicholasfragiskatos.feedme.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController: NavController,
    hasBackButton: Boolean = false,
    hasSettingsButton: Boolean = false,
    hasShareButton: Boolean = false,
    onShareClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            if (hasSettingsButton) {
                IconButton(onClick = onSettingsClicked) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
            }
            if (hasShareButton) {
                IconButton(onClick = onShareClicked) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share Feedings")
                }
            }
        },
        navigationIcon = {
            if (hasBackButton) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate Back Button"
                    )
                }
            }
        }
    )
}