package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import com.nicholasfragiskatos.feedme.ui.screens.UiState
import com.nicholasfragiskatos.feedme.utils.DateUtils
import com.nicholasfragiskatos.feedme.utils.PreferenceManager
import com.nicholasfragiskatos.feedme.utils.ReportGenerator
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FeedingListScreenViewModel @Inject constructor(
    private val repository: FeedingRepository,
    private val reportGenerator: ReportGenerator,
    preferenceManager: PreferenceManager,
) : ViewModel() {

    val groupState: StateFlow<UiState<Map<LocalDateTime, List<Feeding>>>> =
        repository.getFeedings().map {
            val groupBy = it.groupBy { feeding ->
                DateUtils.convertToLocalDate(feeding.date).atStartOfDay()
            }
            UiState(groupBy, false)
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            initialValue = UiState(emptyMap(), true),
            started = SharingStarted.WhileSubscribed(5000),
        )

    private val feedingsForToday = repository.getFeedingsForToday().stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5000),
    )

    val preferences = preferenceManager.getPreferences().stateIn(
        scope = viewModelScope,
        initialValue = FeedMePreferences(
            50f,
            UnitOfMeasurement.MILLILITER,
            UnitOfMeasurement.MILLILITER,
        ),
        started = SharingStarted.WhileSubscribed(5000),
    )

    val graphPoints: StateFlow<List<Point>> =
        feedingsForToday.combine(preferences) { feedings, prefs ->
            createPoints(feedings, prefs.displayUnit)
        }.flowOn(Dispatchers.Default)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                MutableList(1440) {
                    Point(it.toFloat(), 0.0f)
                },
            )

    private val _daySummaryState: MutableStateFlow<DaySummaryState> =
        MutableStateFlow(DaySummaryState())
    val daySummaryState: StateFlow<DaySummaryState> = _daySummaryState.asStateFlow()

    fun deleteFeeding(feeding: Feeding) {
        viewModelScope.launch {
            repository.deleteFeeding(feeding)
        }
    }

    private fun createPoints(feedings: List<Feeding>, displayUnit: UnitOfMeasurement): List<Point> {
        val list = MutableList(1440) { index -> Point(index.toFloat(), 0.0f) }
        var hour: Int
        var minute: Int

        for (feeding in feedings) {
            hour = feeding.date.hours
            minute = feeding.date.minutes

            val absoluteMinute = (hour * 60) + minute
            val amount = UnitUtils.convertMeasurement(feeding.quantity, feeding.unit, displayUnit)

            list[absoluteMinute] = Point(absoluteMinute.toFloat(), amount.toFloat())
        }
        for (index in 1 until list.size) {
            val cur = list[index].y
            val prev = list[index - 1].y
            list[index] = list[index].copy(y = prev + cur)
        }
        return list
    }

    fun generateDaySummary(
        date: LocalDateTime,
        is24HourFormat: Boolean,
        displayUnit: UnitOfMeasurement,
        onSuccess: (String) -> Unit = {},
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _daySummaryState.value = _daySummaryState.value.copy(date = date, loading = true)
            val summary = reportGenerator.generateDaySummary(
                DateUtils.convertToDate(date),
                displayUnit,
                is24HourFormat,
            )
            delay(2000)
            _daySummaryState.value =
                _daySummaryState.value.copy(loading = false, date = null)

            withContext(Dispatchers.Main) {
                onSuccess(summary)
            }
        }
    }
}
