package com.nicholasfragiskatos.feedme.ui.screens.dayoverview

import com.nicholasfragiskatos.feedme.ui.screens.dayoverview.statistics.Statistic

data class DayOverview(
    val min: Statistic,
    val max: Statistic,
    val avg: Statistic,
    val perFeeding: Statistic,
    val perHour: Statistic,
    val total: Statistic,
)
