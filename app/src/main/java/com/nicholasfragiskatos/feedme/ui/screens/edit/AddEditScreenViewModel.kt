package com.nicholasfragiskatos.feedme.ui.screens.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nicholasfragiskatos.feedme.FeedMeApplication
import com.nicholasfragiskatos.feedme.data.local.FeedingDao
import com.nicholasfragiskatos.feedme.data.mapper.toFeedingEntity
import com.nicholasfragiskatos.feedme.domain.Feeding
import com.nicholasfragiskatos.feedme.domain.UnitOfMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AddEditScreenViewModel(val dao: FeedingDao) : ViewModel() {

    private val _date = MutableStateFlow(Date())
    val date: StateFlow<Date> = _date

    private val _quantity = MutableStateFlow("")
    val quantity: StateFlow<String> = _quantity

    private val _units = MutableStateFlow(UnitOfMeasurement.MILLILITER)
    val units: StateFlow<UnitOfMeasurement> = _units

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private var currentFeedingId: Int? = null

    fun saveFeeding() {
        viewModelScope.launch(Dispatchers.IO) {
            val toSave = Feeding(
                date = date.value,
                quantity = quantity.value.toDouble(),
                unit = units.value,
                notes = notes.value
            )
            dao.saveFeeding(toSave.toFeedingEntity())
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val database =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FeedMeApplication).database
                AddEditScreenViewModel(database.feedingDao())
            }
        }
    }
}
