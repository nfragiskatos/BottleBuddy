package com.nicholasfragiskatos.feedme.ui.screens.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import com.nicholasfragiskatos.feedme.utils.DateUtils
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                        _date.value = feeding.date
                    }
                }
            }
        }
    }

    fun saveFeeding(
        generateSummary: Boolean = false,
        is24HourFormat: Boolean = false,
        displayUnit: UnitOfMeasurement = UnitOfMeasurement.MILLILITER,
        onSuccess: (String?) -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var summary: String? = null
            if (generateSummary) {
                val feedingsForDay = repository.getFeedingsByDay(date.value)
                val existingTotal = feedingsForDay.sumOf {
                    UnitUtils.convertMeasurement(
                        it.quantity,
                        it.unit,
                        displayUnit
                    )
                }
                val dayTotal = existingTotal + UnitUtils.convertMeasurement(
                    quantity.value.toDouble(),
                    units.value,
                    displayUnit
                )
                val formattedDate = DateUtils.getFormattedDateWithTime(date.value, is24HourFormat)
                val dayTotalDisplay = "%.2f".format(dayTotal)
                val sb =
                    StringBuilder("$formattedDate\nThis Feeding: ${quantity.value}${units.value.abbreviation}\nDay Total: $dayTotalDisplay")
                if (notes.value.isNotBlank()) {
                    sb.append("\n----\n$notes")
                }
                summary = sb.toString()
            }

            val toSave = Feeding(
                id = _currentFeedingId.value,
                date = date.value,
                quantity = quantity.value.toDouble(),
                unit = units.value,
                notes = notes.value,
            )
            repository.saveFeeding(toSave)
            withContext(Dispatchers.Main) {
                onSuccess(summary)
            }
        }
    }

    fun deleteFeeding(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteFeeding(Feeding(id = _currentFeedingId.value))
            onSuccess()
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
                _quantity.value = UnitUtils.sanitizeDecimalInput(event.quantity, 2)
            }

            is AddEditFeedingEvent.ChangeUnits -> {
                _units.value = event.units
            }
        }
    }
}
