package com.nicholasfragiskatos.feedme.ui.screens.edit

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FakeFeedingRepository
import com.nicholasfragiskatos.feedme.utils.FakePreferenceManager
import com.nicholasfragiskatos.feedme.utils.FakeReportGenerator
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
    private var reportGenerator = FakeReportGenerator()

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
        reportGenerator = FakeReportGenerator()
    }

    @Test
    fun `Test initial state for new feeding`() = runTest {
        // GIVEN: No existing Feeding
        // WHEN: Freshly creating a viewmodel
        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            reportGenerator = reportGenerator,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        // THEN: State should have proper initial values
        assertThat(vm.isAdd).isTrue()
        assertThat(vm.units.value).isEqualTo(UnitOfMeasurement.MILLILITER)

        preferenceManager.emitFromDataFlow(UnitOfMeasurement.OUNCE.name)
        assertThat(vm.units.value).isEqualTo(UnitOfMeasurement.OUNCE)
    }

    @Test
    fun `Test initial state for existing feeding`() = runTest {
        // GIVEN: A valid id of an existing Feeding
        val id = 120L

        // WHEN: Creating a new view model seeded with the id
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
            reportGenerator = reportGenerator,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        // THEN: All state should be initialized based off the Feeding
        assertThat(vm.isAdd).isFalse()
        assertThat(vm.date.value).isEqualTo(date)
        assertThat(vm.quantity.value).isEqualTo(quantity.toString())
        assertThat(vm.units.value).isEqualTo(unit)
        assertThat(vm.notes.value).isEqualTo(notes)
    }

    @Test
    fun `test updating date`() {
        // GIVEN: An initialized viewmodel
        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            reportGenerator = reportGenerator,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        // WHEN: Sending a date update event to the viewmodel
        val date = Date(123, 5, 15)
        vm.onEvent(AddEditFeedingEvent.ChangeDate(date))

        // THEN: The date state should be properly updated
        assertThat(vm.date.value).isEqualTo(date)
    }

    @Test
    fun `test updating notes`() {
        // GIVEN: An initialized viewmodel
        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            reportGenerator = reportGenerator,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        // WHEN: Sending a note update event to the viewmodel
        val notes = "My testing notes"
        vm.onEvent(AddEditFeedingEvent.ChangeNote(notes))

        // THEN: The notes state should be properly updated
        assertThat(vm.notes.value).isEqualTo(notes)
    }

    @Test
    fun `test updating quantity`() {
        // GIVEN: An initialized viewmodel
        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            reportGenerator = reportGenerator,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        // WHEN: Sending a quantity update event to the viewmodel
        val quantity = "12.45"
        vm.onEvent(AddEditFeedingEvent.ChangeQuantity(quantity))

        // THEN: The quantity state should be properly updated
        assertThat(vm.quantity.value).isEqualTo(quantity)
    }

    @Test
    fun `test updating units`() {
        // GIVEN: An initialized viewmodel
        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            reportGenerator = reportGenerator,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        // WHEN: Sending a quantity update event to the viewmodel
        var units = if (vm.units.value == UnitOfMeasurement.MILLILITER) UnitOfMeasurement.OUNCE else UnitOfMeasurement.MILLILITER
        vm.onEvent(AddEditFeedingEvent.ChangeUnits(units))

        // THEN: The units state should be propery updated
        assertThat(vm.units.value).isEqualTo(units)
    }

    @Test
    fun `test save feeding`() {

        // GIVEN: A viewmodel with updated state
        val quantity = "35.4"
        val date = Date(123, 7, 20)
        val units = UnitOfMeasurement.MILLILITER
        val notes = "My Test Notes"

        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            reportGenerator = reportGenerator,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        vm.onEvent(AddEditFeedingEvent.ChangeDate(date))
        vm.onEvent(AddEditFeedingEvent.ChangeUnits(units))
        vm.onEvent(AddEditFeedingEvent.ChangeQuantity(quantity))
        vm.onEvent(AddEditFeedingEvent.ChangeNote(notes))

        // WHEN: Saving the feeding
        vm.saveFeeding()

        // THEN: The correct Feeding object should have been saved.
        val savedFeeding = repo.savedFeeding
        assertThat(savedFeeding).isNotNull()
        assertThat(savedFeeding?.date).isEqualTo(date)
        assertThat(savedFeeding?.quantity).isEqualTo(quantity.toDouble())
        assertThat(savedFeeding?.unit).isEqualTo(units)
        assertThat(savedFeeding?.notes).isEqualTo(notes)
    }

    @Test
    fun `test feeding summary was generated`() = runTest {
        // GIVEN: A viewmodel with updated state
        val quantity = "35.4"
        val date = Date(123, 7, 20)
        val units = UnitOfMeasurement.MILLILITER
        val notes = "My Test Notes"

        val vm = AddEditScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            reportGenerator = reportGenerator,
            preferenceManager = preferenceManager,
            savedStateHandle = savedStateHandle,
        )

        vm.onEvent(AddEditFeedingEvent.ChangeDate(date))
        vm.onEvent(AddEditFeedingEvent.ChangeUnits(units))
        vm.onEvent(AddEditFeedingEvent.ChangeQuantity(quantity))
        vm.onEvent(AddEditFeedingEvent.ChangeNote(notes))

        // WHEN: Saving the Feeding and flagged to create a summary report
        vm.saveFeeding(generateSummary = true) {

            // THEN: The summary should be generated, and not blank
            assertThat(it).isNotNull()
            assertThat(it!!.isNotBlank()).isTrue()
        }
    }
}
