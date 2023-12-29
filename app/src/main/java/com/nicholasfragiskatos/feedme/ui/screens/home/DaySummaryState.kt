package com.nicholasfragiskatos.feedme.ui.screens.home

import java.time.LocalDateTime

data class DaySummaryState(
    val date: LocalDateTime? = null,
    val loading: Boolean = false,
)