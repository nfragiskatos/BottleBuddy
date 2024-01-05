package com.nicholasfragiskatos.feedme.utils.reports

import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import java.util.Date

interface ReportGenerator {
    suspend fun generateFeedingSummary(
        feeding: Feeding,
        displayUnit: UnitOfMeasurement,
        is24HourFormat: Boolean,
    ): String

    suspend fun generateDaySummary(
        date: Date,
        displayUnit: UnitOfMeasurement,
        is24HourFormat: Boolean,
    ): String
}
