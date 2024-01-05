package com.nicholasfragiskatos.feedme.utils

import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.utils.reports.ReportGenerator
import java.util.Date

class FakeReportGenerator : ReportGenerator {
    override suspend fun generateFeedingSummary(
        feeding: Feeding,
        displayUnit: UnitOfMeasurement,
        is24HourFormat: Boolean,
    ): String {
        return "Fake Report"
    }

    override suspend fun generateDaySummary(
        date: Date,
        displayUnit: UnitOfMeasurement,
        is24HourFormat: Boolean,
    ): String {
        return "Fake Day Summary"
    }
}
