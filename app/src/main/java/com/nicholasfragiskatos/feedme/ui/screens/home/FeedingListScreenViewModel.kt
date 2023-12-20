package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import com.nicholasfragiskatos.feedme.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
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

    private val _graphPoints: MutableStateFlow<List<Point>> = MutableStateFlow(MutableList(1440) {
        Point(it.toFloat(), 0.0f)
    })
    val graphPoints: StateFlow<List<Point>> = _graphPoints.asStateFlow()

    val dailyGoal =
        preferenceManager.getData(PreferenceManager.GOAL_KEY_DATA_STORE, "0.0").map {
            try {
                it.toFloat()
            } catch (e: Exception) {
                0f
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = 0f,
            started = SharingStarted.WhileSubscribed(5000L)
        )

    init {
        viewModelScope.launch {
            _loading.value = true
            repository.getFeedings()
                .collect {

                    val groupBy: Map<LocalDateTime, List<Feeding>> = it.groupBy {
                        val toLocalDate =
                            it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        toLocalDate.atStartOfDay()
                    }
                    _grouping.value = groupBy

                    val today = LocalDate.now().atStartOfDay()
                    val feedingsToday = groupBy[today] ?: emptyList()
                    _loading.value = false
                    _graphPoints.value = createPoints(feedingsToday)
                }

        }
    }

    fun deleteFeeding(feeding: Feeding) {
        viewModelScope.launch {
            repository.deleteFeeding(feeding)
        }
    }

    private fun createPoints(feedings: List<Feeding>): List<Point> {
        val list = MutableList(1440) { index -> Point(index.toFloat(), 0.0f) }
        var hour = 0
        var minute = 0

        for (feeding in feedings) {
            hour = feeding.date.hours
            minute = feeding.date.minutes

            val absoluteMinute = (hour * 60) + minute

            list[absoluteMinute] = Point(absoluteMinute.toFloat(), feeding.quantity.toFloat())
        }
        for (index in 1 until list.size) {
            val cur = list[index].y
            val prev = list[index - 1].y
            list[index] = list[index].copy(y = prev + cur)
        }
        return list
    }
}
