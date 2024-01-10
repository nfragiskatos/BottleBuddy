package com.nicholasfragiskatos.feedme.ui.screens.dayoverview


import android.text.format.DateUtils
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import com.nicholasfragiskatos.feedme.ui.screens.dayoverview.statistics.Statistic
import com.nicholasfragiskatos.feedme.ui.screens.dayoverview.statistics.StatisticType
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import com.nicholasfragiskatos.feedme.utils.dispatchers.DispatcherProvider
import com.nicholasfragiskatos.feedme.utils.preferences.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DayOverviewScreenViewModel @Inject constructor(
    private val repository: FeedingRepository,
    private val dispatcherProvider: DispatcherProvider,
    preferenceManager: PreferenceManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _date: MutableStateFlow<Date> = MutableStateFlow(Date())
    val date: StateFlow<Date> = _date.asStateFlow()

    private val _feedingsForDay: MutableStateFlow<List<Feeding>> = MutableStateFlow(emptyList())
    val feedingsForDay: StateFlow<List<Feeding>> = _feedingsForDay.asStateFlow()

    val preferences = preferenceManager.getPreferences().stateIn(
        scope = viewModelScope,
        initialValue = FeedMePreferences(
            50f,
            UnitOfMeasurement.MILLILITER,
            UnitOfMeasurement.MILLILITER,
        ),
        started = SharingStarted.WhileSubscribed(5000)
    )

    val graphPoints: StateFlow<List<Point>> =
        feedingsForDay.combine(preferences) { feedings, pref ->
            createPoints(feedings, pref.displayUnit)
        }.flowOn(dispatcherProvider.default)
            .stateIn(
                scope = viewModelScope,
                initialValue = MutableList(1440) {
                    Point(it.toFloat(), 0.0f)
                },
                started = SharingStarted.WhileSubscribed(5000)
            )

    val largestFeeding: StateFlow<Statistic> =
        feedingsForDay.combine(preferences) { feedings, pref ->
            val max = feedings.maxOf { feeding ->
                UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, pref.displayUnit)
            }
            Statistic(
                value = max,
                unit = preferences.value.displayUnit,
                type = StatisticType.MAX
            )
        }.flowOn(dispatcherProvider.default)
            .stateIn(
                scope = viewModelScope,
                initialValue = Statistic(
                    value = 0.0,
                    unit = preferences.value.displayUnit,
                    type = StatisticType.MAX
                ),
                started = SharingStarted.WhileSubscribed(5000)
            )

    val smallestFeeding: StateFlow<Statistic> =
        feedingsForDay.combine(preferences) { feedings, pref ->
            val min = feedings.minOf { feeding ->
                UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, pref.displayUnit)
            }
            Statistic(
                value = min,
                unit = preferences.value.displayUnit,
                type = StatisticType.MIN
            )
        }.flowOn(dispatcherProvider.default)
            .stateIn(
                scope = viewModelScope,
                initialValue = Statistic(
                    value = 0.0,
                    unit = preferences.value.displayUnit,
                    type = StatisticType.MIN
                ),
                started = SharingStarted.WhileSubscribed(5000)
            )

    val averageFeeding: StateFlow<Statistic> =
        feedingsForDay.combine(preferences) { feedings, pref ->
            val avg = feedings.map { feeding ->
                UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, pref.displayUnit)
            }.average()
            Statistic(
                value = avg,
                unit = pref.displayUnit,
                type = StatisticType.AVG
            )
        }.flowOn(dispatcherProvider.default)
            .stateIn(
                scope = viewModelScope,
                initialValue = Statistic(
                    value = 0.0,
                    unit = preferences.value.displayUnit,
                    type = StatisticType.AVG
                ),
                started = SharingStarted.WhileSubscribed(5000)
            )

    val perFeeding: StateFlow<Statistic> =
        feedingsForDay.combine(preferences) {feedings, pref ->
            val sum = feedings.sumOf { feeding ->
                UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, pref.displayUnit)
            }
            Statistic(
                value = if (feedings.isEmpty()) 0.0 else sum / feedings.size,
                unit = pref.displayUnit,
                type = StatisticType.PER_FEEDING
            )

        }.flowOn(dispatcherProvider.default)
            .stateIn(
                scope = viewModelScope,
                initialValue = Statistic(
                    value = 0.0,
                    unit = preferences.value.displayUnit,
                    type = StatisticType.PER_FEEDING
                ),
                started = SharingStarted.WhileSubscribed(5000)
            )

    val perHour: StateFlow<Statistic> =
        feedingsForDay.combine(preferences) { feedings, pref ->
            var sum = 0.0
            var start = feedings.first().date
            var end = Date(start.time)
            feedings.forEach { feeding ->
                sum += UnitUtils.convertMeasurement(
                    feeding.quantity,
                    feeding.unit,
                    pref.displayUnit
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
            Statistic(
                value = sum / hours,
                unit = pref.displayUnit,
                type = StatisticType.PER_HOUR
            )
        }.flowOn(dispatcherProvider.default)
            .stateIn(
                scope = viewModelScope,
                initialValue = Statistic(
                    value = 0.0,
                    unit = preferences.value.displayUnit,
                    type = StatisticType.PER_HOUR
                ),
                started = SharingStarted.WhileSubscribed(5000)
            )

    val total: StateFlow<Statistic> =
        feedingsForDay.combine(preferences) { feedings, pref ->
            val sum = feedings.sumOf { feeding ->
                UnitUtils.convertMeasurement(
                    feeding.quantity,
                    feeding.unit,
                    pref.displayUnit
                )
            }
            Statistic(
                value = sum,
                unit = pref.displayUnit,
                type = StatisticType.Total
            )
        }.flowOn(dispatcherProvider.default)
            .stateIn(
                scope = viewModelScope,
                initialValue = Statistic(
                    value = 0.0,
                    unit = preferences.value.displayUnit,
                    type = StatisticType.Total
                ),
                started = SharingStarted.WhileSubscribed(5000)
            )

    init {
        val timestamp = savedStateHandle.get<Long>("timestamp")

        timestamp?.let {
            _date.value = Date(it)
            viewModelScope.launch(dispatcherProvider.io) {
                val feedingsByDay = repository.getFeedingsByDay(date.value)
                _feedingsForDay.value = feedingsByDay
            }
        }
    }

    private fun createPoints(feedings: List<Feeding>, displayUnit: UnitOfMeasurement): List<Point> {
        val list = MutableList(1440) { index ->
            Point(
                index.toFloat(),
                0.0f
            )
        }
        var hour: Int
        var minute: Int

        for (feeding in feedings) {
            hour = feeding.date.hours
            minute = feeding.date.minutes

            val absoluteMinute = (hour * 60) + minute
            val amount = UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, displayUnit)

            list[absoluteMinute] =
                Point(absoluteMinute.toFloat(), amount.toFloat())
        }
        for (index in 1 until list.size) {
            val cur = list[index].y
            val prev = list[index - 1].y
            list[index] = list[index].copy(y = prev + cur)
        }
        return list
    }
}