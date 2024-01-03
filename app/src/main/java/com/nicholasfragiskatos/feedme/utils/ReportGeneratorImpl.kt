package com.nicholasfragiskatos.feedme.utils

import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import javax.inject.Inject

class ReportGeneratorImpl @Inject constructor(
    private val repository: FeedingRepository,
) : ReportGenerator {

    override suspend fun generateFeedingSummary(
        feeding: Feeding,
        displayUnit: UnitOfMeasurement,
        is24HourFormat: Boolean,
    ): String {
        val alternateDisplayUnit =
            if (displayUnit == UnitOfMeasurement.MILLILITER) UnitOfMeasurement.OUNCE else UnitOfMeasurement.MILLILITER
        val feedingsForDay = repository.getFeedingsByDay(feeding.date)
        val feedingsForDayTotal = feedingsForDay.sumOf {
            UnitUtils.convertMeasurement(
                it.quantity,
                it.unit,
                displayUnit,
            )
        }

        val dayTotal = feedingsForDayTotal + UnitUtils.convertMeasurement(
            feeding.quantity,
            feeding.unit,
            displayUnit,
        )
        val dayTotalDisplay = UnitUtils.format(dayTotal, displayUnit)

        val alternateDayTotal =
            UnitUtils.convertMeasurement(dayTotal, displayUnit, alternateDisplayUnit)
        val alternateDayTotalDisplay =
            UnitUtils.format(alternateDayTotal, alternateDisplayUnit)

        val normalizedQuantity =
            UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, displayUnit)
        val normalizedQuantityDisplay = UnitUtils.format(normalizedQuantity, displayUnit)

        val sb =
            StringBuilder(DateUtils.getFormattedDateWithTime(feeding.date, is24HourFormat))
        sb.append("\nThis Feeding: ${normalizedQuantityDisplay}${displayUnit.abbreviation}")
        sb.append("\nDay Total: $dayTotalDisplay${displayUnit.abbreviation} ($alternateDayTotalDisplay${alternateDisplayUnit.abbreviation})")

        if (feeding.notes.isNotBlank()) {
            sb.append("\n----\n${feeding.notes}")
        }

        return sb.toString()
    }
}
