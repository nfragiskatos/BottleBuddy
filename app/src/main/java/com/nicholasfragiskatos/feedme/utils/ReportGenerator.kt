package com.nicholasfragiskatos.feedme.utils

import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement

interface ReportGenerator {
    suspend fun generateFeedingSummary(
        feeding: Feeding,
        displayUnit: UnitOfMeasurement,
        is24HourFormat: Boolean,
    ): String
}