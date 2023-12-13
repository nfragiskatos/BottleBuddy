package com.nicholasfragiskatos.feedme.ui.screens.edit

import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import java.util.Date

sealed class AddEditFeedingEvent {
    data class ChangeDate(val date: Date) : AddEditFeedingEvent()
    data class ChangeQuantity(val quantity: String) : AddEditFeedingEvent()
    data class ChangeUnits(val units: UnitOfMeasurement) : AddEditFeedingEvent()
    data class ChangeNote(val notes: String) : AddEditFeedingEvent()
}
