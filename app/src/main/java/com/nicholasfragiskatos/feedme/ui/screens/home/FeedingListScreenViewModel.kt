package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedingListScreenViewModel @Inject constructor(
    private val repository: FeedingRepository,
) : ViewModel() {

    private val _feedings: MutableStateFlow<List<Feeding>> = MutableStateFlow(emptyList())
    val feedings: StateFlow<List<Feeding>>
        get() = _feedings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getFeedings()
                .collect {
                    _feedings.value = it
                }
        }
    }
}
