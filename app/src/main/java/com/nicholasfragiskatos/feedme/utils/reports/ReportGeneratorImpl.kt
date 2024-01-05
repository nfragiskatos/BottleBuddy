package com.nicholasfragiskatos.feedme.utils.reports

import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import com.nicholasfragiskatos.feedme.utils.dates.DateFormatter
import java.util.Date
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
            StringBuilder(DateFormatter.getFormattedDateWithTime(feeding.date, is24HourFormat))
        sb.append("\nThis Feeding: ${normalizedQuantityDisplay}${displayUnit.abbreviation}")
        sb.append("\nDay Total: $dayTotalDisplay${displayUnit.abbreviation} ($alternateDayTotalDisplay${alternateDisplayUnit.abbreviation})")

        if (feeding.notes.isNotBlank()) {
            sb.append("\n----\n${feeding.notes}")
        }

        return sb.toString()
    }

    override suspend fun generateDaySummary(
        date: Date,
        displayUnit: UnitOfMeasurement,
        is24HourFormat: Boolean,
    ): String {
        val alternateDisplayUnit =
            if (displayUnit == UnitOfMeasurement.MILLILITER) UnitOfMeasurement.OUNCE else UnitOfMeasurement.MILLILITER
        val feedings = repository.getFeedingsByDay(date)

        return if (feedings.isNotEmpty()) {
            var dayTotal = 0.0
            val sb = StringBuilder("Summary for ${DateFormatter.getFormattedDate(date)}\n")
            for (index in feedings.indices.reversed()) {
                val feeding = feedings[index]
                val quantity = UnitUtils.convertMeasurement(
                    feeding.quantity,
                    feeding.unit,
                    displayUnit,
                )
                val quantityDisplay = UnitUtils.format(quantity, displayUnit)

                dayTotal += quantity

                val formattedDate = DateFormatter.getFormattedTime(feeding.date, is24HourFormat)
                sb.append("\n$formattedDate - $quantityDisplay${displayUnit.abbreviation}")
            }
            sb.append("\n------------")

            val dayTotalDisplay = UnitUtils.format(dayTotal, displayUnit)

            val alternateDayTotal =
                UnitUtils.convertMeasurement(dayTotal, displayUnit, alternateDisplayUnit)
            val alternateDayTotalDisplay =
                UnitUtils.format(alternateDayTotal, alternateDisplayUnit)

            sb.append("\nTotal: $dayTotalDisplay${displayUnit.abbreviation} ($alternateDayTotalDisplay${alternateDisplayUnit.abbreviation})")

            sb.toString()
        } else {
            "No feedings logged for this day."
        }
    }
}
