package com.nicholasfragiskatos.feedme.ui.screens

import androidx.annotation.StringRes
import com.nicholasfragiskatos.feedme.R

sealed class NavigationItem(val route: String, @StringRes val resourceId: Int) {
    object Home : NavigationItem("home", R.string.home_screen_title)
    object Edit : NavigationItem("edit", R.string.edit_screen_title) {
        fun buildRoute(feedingId: Long): String {
            return "edit/$feedingId"
        }
    }
}
