package com.nicholasfragiskatos.feedme.ui.screens.dayoverview


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
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
        feedingsForDay.combine(preferences) {feedings, pref ->
            createPoints(feedings, pref.displayUnit)
        }.flowOn(dispatcherProvider.default)
            .stateIn(
                scope = viewModelScope,
                initialValue = MutableList(1440) {
                    Point(it.toFloat(), 0.0f)
                },
                started = SharingStarted.WhileSubscribed(5000)
            )

    init {
        val timestamp = savedStateHandle.get<Long>("timestamp")

        timestamp?.let {
            viewModelScope.launch(dispatcherProvider.io) {
                val feedingsByDay = repository.getFeedingsByDay(Date(it))
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