package com.nicholasfragiskatos.feedme.ui.screens.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditScreenViewModel @Inject constructor(
    private val repository: FeedingRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _currentFeedingId = MutableStateFlow(-1L)

    private val _date = MutableStateFlow(Date())
    val date: StateFlow<Date> = _date

    private val _quantity = MutableStateFlow("")
    val quantity: StateFlow<String> = _quantity

    private val _units = MutableStateFlow(UnitOfMeasurement.MILLILITER)
    val units: StateFlow<UnitOfMeasurement> = _units

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private val _finished = MutableStateFlow(false)
    val finished: StateFlow<Boolean>
        get() = _finished.asStateFlow()

    val isAdd
        get() = _currentFeedingId.value == 0L

    init {
        savedStateHandle.get<Long>("feedingId")?.let { feedingId ->

            _currentFeedingId.value = feedingId
            if (!isAdd) {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.getFeedingById(feedingId)?.let { feeding ->
                        _notes.value = feeding.notes
                        _quantity.value = feeding.quantity.toString()
                        _units.value = feeding.unit
                        _currentFeedingId.value = feeding.id
                    }
                }
            }
        }
    }

    fun saveFeeding() {
        viewModelScope.launch(Dispatchers.IO) {
            val toSave = Feeding(
                id = _currentFeedingId.value,
                date = date.value,
                quantity = quantity.value.toDouble(),
                unit = units.value,
                notes = notes.value,
            )
            repository.saveFeeding(toSave)
            _finished.value = true
        }
    }

    fun deleteFeeding() {
        viewModelScope.launch {
            repository.deleteFeeding(Feeding(id = _currentFeedingId.value))
            _finished.value = true
        }
    }

    fun onEvent(event: AddEditFeedingEvent) {
        when (event) {
            is AddEditFeedingEvent.ChangeDate -> {
                _date.value = event.date
            }

            is AddEditFeedingEvent.ChangeNote -> {
                _notes.value = event.notes
            }

            is AddEditFeedingEvent.ChangeQuantity -> {
                _quantity.value = event.quantity
            }

            is AddEditFeedingEvent.ChangeUnits -> {
                _units.value = event.units
            }
        }
    }
}
