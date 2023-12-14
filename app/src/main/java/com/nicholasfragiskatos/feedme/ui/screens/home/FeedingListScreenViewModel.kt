package com.nicholasfragiskatos.feedme.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class FeedingListScreenViewModel @Inject constructor(
    private val repository: FeedingRepository,
) : ViewModel() {

    private val _feedings: MutableStateFlow<List<Feeding>> = MutableStateFlow(emptyList())
    val feedings: StateFlow<List<Feeding>>
        get() = _feedings.asStateFlow()

    private val _grouping: MutableStateFlow<Map<LocalDateTime, List<Feeding>>> = MutableStateFlow(
        emptyMap(),
    )
    val grouping: StateFlow<Map<LocalDateTime, List<Feeding>>>
        get() = _grouping.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFeedings()
                .collect {
                    _feedings.value = it

                    val groupBy: Map<LocalDateTime, List<Feeding>> = it.groupBy {
                        val toLocalDate =
                            it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        toLocalDate.atStartOfDay()
                    }
                    _grouping.value = groupBy
                    Log.d("FeedingListScreenViewModel", groupBy.toString())
                }
        }
    }

    fun deleteFeeding(feeding: Feeding) {
        viewModelScope.launch {
            repository.deleteFeeding(feeding)
        }
    }
}
