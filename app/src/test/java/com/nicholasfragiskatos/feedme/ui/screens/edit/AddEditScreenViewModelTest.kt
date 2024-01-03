package com.nicholasfragiskatos.feedme.ui.screens.edit

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FakeFeedingRepository
import com.nicholasfragiskatos.feedme.utils.FakePreferenceManager
import com.nicholasfragiskatos.feedme.utils.TestDispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditScreenViewModelTest {
    private val dispatcherProvider = TestDispatcherProvider()
    private var repo = FakeFeedingRepository()
    private var preferenceManager = FakePreferenceManager()
    private var savedStateHandle = SavedStateHandle(mapOf())

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcherProvider.main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        repo = FakeFeedingRepository()
        preferenceManager = FakePreferenceManager()
        savedStateHandle = SavedStateHandle(mapOf())
    }

    @Test
    fun `Test initial state for new feeding`() = runTest {
        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )
        assertThat(vm.isAdd).isTrue()
        assertThat(vm.units.value).isEqualTo(UnitOfMeasurement.MILLILITER)

        preferenceManager.emitFromDataFlow(UnitOfMeasurement.OUNCE.name)
        assertThat(vm.units.value).isEqualTo(UnitOfMeasurement.OUNCE)
    }

    @Test
    fun `Test initial state for existing feeding`() = runTest {
        val id = 120L
        savedStateHandle = SavedStateHandle(mapOf("feedingId" to id))

        val date = Date()
        val quantity = 125.0
        val unit = UnitOfMeasurement.OUNCE
        val notes = "My notes"

        repo.feeding = Feeding(
            id = id,
            date = date,
            quantity = quantity,
            unit = unit,
            notes = notes,
        )

        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        assertThat(vm.date.value).isEqualTo(date)
        assertThat(vm.quantity.value).isEqualTo(quantity.toString())
        assertThat(vm.units.value).isEqualTo(unit)
        assertThat(vm.notes.value).isEqualTo(notes)
    }

    @Test
    fun `test updating date`() {
        val date = Date(123, 5, 15)

        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        vm.onEvent(AddEditFeedingEvent.ChangeDate(date))

        assertThat(vm.date.value).isEqualTo(date)
    }

    @Test
    fun `test updating notes`() {
        val notes = "My testing notes"

        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        vm.onEvent(AddEditFeedingEvent.ChangeNote(notes))

        assertThat(vm.notes.value).isEqualTo(notes)
    }

    @Test
    fun `test updating quantity`() {
        val quantity = "12.45"

        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        vm.onEvent(AddEditFeedingEvent.ChangeQuantity(quantity))

        assertThat(vm.quantity.value).isEqualTo(quantity)
    }

    @Test
    fun `test updating units`() {
        var units = UnitOfMeasurement.MILLILITER

        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        vm.onEvent(AddEditFeedingEvent.ChangeUnits(units))

        assertThat(vm.units.value).isEqualTo(units)

        units = UnitOfMeasurement.OUNCE
        vm.onEvent(AddEditFeedingEvent.ChangeUnits(units))

        assertThat(vm.units.value).isEqualTo(units)
    }

    @Test
    fun `test save feeding`() {
        val quantity = "35.4"
        val date = Date(123, 7, 20)
        val units = UnitOfMeasurement.MILLILITER
        val notes = "My Test Notes"

        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        vm.onEvent(AddEditFeedingEvent.ChangeDate(date))
        vm.onEvent(AddEditFeedingEvent.ChangeUnits(units))
        vm.onEvent(AddEditFeedingEvent.ChangeQuantity(quantity))
        vm.onEvent(AddEditFeedingEvent.ChangeNote(notes))

        vm.saveFeeding()

        val savedFeeding = repo.savedFeeding
        assertThat(savedFeeding).isNotNull()
        assertThat(savedFeeding?.date).isEqualTo(date)
        assertThat(savedFeeding?.quantity).isEqualTo(quantity.toDouble())
        assertThat(savedFeeding?.unit).isEqualTo(units)
        assertThat(savedFeeding?.notes).isEqualTo(notes)
    }
}
