package com.nicholasfragiskatos.feedme.ui.screens.dayoverview.util

import android.text.format.DateUtils
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.ui.screens.dayoverview.DayOverview
import com.nicholasfragiskatos.feedme.ui.screens.dayoverview.statistics.Statistic
import com.nicholasfragiskatos.feedme.ui.screens.dayoverview.statistics.StatisticType
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import java.util.Date

object StatisticsCalculator {

    fun getDayOverview(feedings: List<Feeding>, displayUnit: UnitOfMeasurement): DayOverview {
        return DayOverview(
            min = getMin(feedings, displayUnit),
            max = getMax(feedings, displayUnit),
            avg = getAverage(feedings, displayUnit),
            perFeeding = getPerFeeding(feedings, displayUnit),
            perHour = getPerHour(feedings, displayUnit),
            total = getTotal(feedings, displayUnit)
        )
    }

    private fun getMin(feedings: List<Feeding>, displayUnit: UnitOfMeasurement): Statistic {
        val min = feedings.minOf { feeding ->
            UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, displayUnit)
        }
        return Statistic(
            value = min,
            unit = displayUnit,
            type = StatisticType.MIN
        )
    }

    private fun getMax(feedings: List<Feeding>, displayUnit: UnitOfMeasurement): Statistic {
        val max = feedings.maxOf { feeding ->
            UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, displayUnit)
        }
        return Statistic(
            value = max,
            unit = displayUnit,
            type = StatisticType.MAX
        )
    }

    private fun getAverage(feedings: List<Feeding>, displayUnit: UnitOfMeasurement): Statistic {
        val avg = feedings.map { feeding ->
            UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, displayUnit)
        }.average()
        return Statistic(
            value = avg,
            unit = displayUnit,
            type = StatisticType.AVG
        )
    }

    private fun getPerFeeding(feedings: List<Feeding>, displayUnit: UnitOfMeasurement): Statistic {
        val sum = feedings.sumOf { feeding ->
            UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, displayUnit)
        }
        return Statistic(
            value = if (feedings.isEmpty()) 0.0 else sum / feedings.size,
            unit = displayUnit,
            type = StatisticType.PER_FEEDING
        )
    }

    private fun getPerHour(feedings: List<Feeding>, displayUnit: UnitOfMeasurement): Statistic {
        var sum = 0.0
        var start = feedings.first().date
        var end = Date(start.time)
        feedings.forEach { feeding ->
            sum += UnitUtils.convertMeasurement(
                feeding.quantity,
                feeding.unit,
                displayUnit
            )
            if (feeding.date < start) {
                start = feeding.date
            }
            if (feeding.date > end) {
                end = feeding.date
            }
        }
        // If there is only one data point, and it's a previous day,
        // then just assume the end time is the end of the day.
        if (feedings.size == 1 && !DateUtils.isToday(start.time)) {
            end.apply {
                hours = 23
                minutes = 59
                seconds = 59
            }
        }
        val hours = (end.time - start.time) / 3_600_000.0
        return Statistic(
            value = sum / hours,
            unit = displayUnit,
            type = StatisticType.PER_HOUR
        )
    }

    private fun getTotal(feedings: List<Feeding>, displayUnit: UnitOfMeasurement): Statistic {
        val sum = feedings.sumOf { feeding ->
            UnitUtils.convertMeasurement(
                feeding.quantity,
                feeding.unit,
                displayUnit
            )
        }
        return Statistic(
            value = sum,
            unit = displayUnit,
            type = StatisticType.Total
        )
    }
}