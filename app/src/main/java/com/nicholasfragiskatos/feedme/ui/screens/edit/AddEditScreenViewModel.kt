package com.nicholasfragiskatos.feedme.ui.screens.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import com.nicholasfragiskatos.feedme.utils.UnitUtils
import com.nicholasfragiskatos.feedme.utils.dispatchers.DispatcherProvider
import com.nicholasfragiskatos.feedme.utils.preferences.PreferenceManager
import com.nicholasfragiskatos.feedme.utils.reports.ReportGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditScreenViewModel @Inject constructor(
    private val repository: FeedingRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val reportGenerator: ReportGenerator,
    preferenceManager: PreferenceManager,
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

    val preferences = preferenceManager.getPreferences().stateIn(
        scope = viewModelScope,
        initialValue = FeedMePreferences(
            50f,
            UnitOfMeasurement.MILLILITER,
            UnitOfMeasurement.MILLILITER,
        ),
        started = SharingStarted.WhileSubscribed(5000),
    )

    init {
        val feedingId = savedStateHandle.get<Long>("feedingId")
        _currentFeedingId.value = feedingId ?: 0L

        if (feedingId != null && !isAdd) {
            viewModelScope.launch(dispatcherProvider.io) {
                repository.getFeedingById(feedingId)?.let { feeding ->
                    _notes.value = feeding.notes
                    _quantity.value = feeding.quantity.toString()
                    _units.value = feeding.unit
                    _currentFeedingId.value = feeding.id
                    _date.value = feeding.date
                }
            }
        } else {
            viewModelScope.launch {
                preferenceManager.getData(
                    PreferenceManager.PREFERRED_UNIT_KEY_DATA_STORE,
                    UnitOfMeasurement.MILLILITER.name,
                ).collect {
                    _units.value = UnitOfMeasurement.valueOf(it)
                }
            }
        }
    }

    fun saveFeeding(
        generateSummary: Boolean = false,
        is24HourFormat: Boolean = false,
        displayUnit: UnitOfMeasurement = UnitOfMeasurement.MILLILITER,
        onSuccess: (String?) -> Unit = {},
    ) {
        viewModelScope.launch(dispatcherProvider.io) {
            var summary: String? = null

            val toSave = Feeding(
                id = _currentFeedingId.value,
                date = date.value,
                quantity = quantity.value.toDouble(),
                unit = units.value,
                notes = notes.value,
            )

            if (generateSummary) {
                summary =
                    reportGenerator.generateFeedingSummary(toSave, displayUnit, is24HourFormat)
            }

            repository.saveFeeding(toSave)
            withContext(dispatcherProvider.main) {
                onSuccess(summary)
            }
        }
    }

    fun deleteFeeding(onSuccess: () -> Unit = {}) {
        viewModelScope.launch(dispatcherProvider.io) {
            repository.deleteFeeding(Feeding(id = _currentFeedingId.value))
            withContext(dispatcherProvider.main) {
                onSuccess()
            }
        }
    }

    private fun onShareFeeding(
        is24HourFormat: Boolean = false,
        displayUnit: UnitOfMeasurement = UnitOfMeasurement.MILLILITER,
        onSuccess: (String) -> Unit = {}
    ) {
        viewModelScope.launch(dispatcherProvider.default) {
            val toShare = Feeding(
                id = _currentFeedingId.value,
                date = date.value,
                quantity = quantity.value.toDouble(),
                unit = units.value,
                notes = notes.value,
            )
            val summary = reportGenerator.generateFeedingSummary(toShare, displayUnit, is24HourFormat)
            withContext(dispatcherProvider.main) {
                onSuccess(summary)
            }

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

            is AddEditFeedingEvent.OnShareFeeding -> {
                onShareFeeding(
                    is24HourFormat = event.is24HourFormat,
                    displayUnit = event.displayUnit,
                    onSuccess = event.onSuccess
                )
            }
        }
    }
}
