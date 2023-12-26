package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import com.nicholasfragiskatos.feedme.utils.PreferenceManager
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class FeedingListScreenViewModel @Inject constructor(
    private val repository: FeedingRepository,
    preferenceManager: PreferenceManager
) : ViewModel() {

    private val _grouping: MutableStateFlow<Map<LocalDateTime, List<Feeding>>> = MutableStateFlow(
        emptyMap(),
    )
    val grouping: StateFlow<Map<LocalDateTime, List<Feeding>>> = _grouping.asStateFlow()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val feedingsForToday = repository.getFeedingsForToday().stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5000)
    )

    val preferences = preferenceManager.getPreferences().stateIn(
        scope = viewModelScope,
        initialValue = FeedMePreferences(
            50f,
            UnitOfMeasurement.MILLILITER,
            UnitOfMeasurement.MILLILITER
        ),
        started = SharingStarted.WhileSubscribed(5000)
    )

    val graphPoints : StateFlow<List<Point>> = feedingsForToday.combine(preferences) { feedings, prefs ->
        createPoints(feedings, prefs.displayUnit)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MutableList(1440) {
        Point(it.toFloat(), 0.0f)
    })

    init {
        viewModelScope.launch {
            _loading.value = true
            repository.getFeedings()
                .collect {

                    val groupBy: Map<LocalDateTime, List<Feeding>> = it.groupBy { feeding ->
                        val toLocalDate =
                            feeding.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        toLocalDate.atStartOfDay()
                    }
                    _grouping.value = groupBy
                    _loading.value = false
                }

        }
    }

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
}
