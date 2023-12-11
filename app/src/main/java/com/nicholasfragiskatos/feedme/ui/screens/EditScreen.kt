package com.nicholasfragiskatos.feedme.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nicholasfragiskatos.feedme.R

@Composable
fun EditScreen(feedId: Int? = null) {
    Column {
//
    }

    Text(text = stringResource(id = R.string.edit_screen_title))
}
