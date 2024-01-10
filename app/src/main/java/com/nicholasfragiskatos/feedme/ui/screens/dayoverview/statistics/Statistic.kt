package com.nicholasfragiskatos.feedme.ui.screens.dayoverview.statistics

import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement

data class Statistic(
    val value: Double,
    val unit: UnitOfMeasurement,
    val type: StatisticType
)
