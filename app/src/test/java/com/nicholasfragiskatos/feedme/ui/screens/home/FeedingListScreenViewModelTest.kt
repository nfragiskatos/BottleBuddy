package com.nicholasfragiskatos.feedme.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.domain.repository.FakeFeedingRepository
import com.nicholasfragiskatos.feedme.utils.FakePreferenceManager
import com.nicholasfragiskatos.feedme.utils.FakeReportGenerator
import com.nicholasfragiskatos.feedme.utils.TestDispatcherProvider
import com.nicholasfragiskatos.feedme.utils.dates.DateConverterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class FeedingListScreenViewModelTest {
    private val scope = TestScope()
    private val dispatcherProvider = TestDispatcherProvider()
    private var repo = FakeFeedingRepository()
    private var preferenceManager = FakePreferenceManager()
    private var savedStateHandle = SavedStateHandle(mapOf())
    private var reportGenerator = FakeReportGenerator()
    private var dateConverter = DateConverterImpl()

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
    fun `test initial empty state`() = runTest {
        // GIVEN: A newly initialized viewmodel
        val vm = FeedingListScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            reportGenerator = reportGenerator,
            dateConverter = dateConverter,
        )

        // WHEN: Observing state
        scope.launch(dispatcherProvider.default) {
            vm.groupState.collect()
            vm.graphPoints.collect()
            vm.daySummaryState.collect()
        }

        // THEN: All state should have proper initial values
        val groupState = vm.groupState.value
        assertThat(groupState.data).isEmpty()
        assertThat(groupState.isLoading).isTrue()

        val graphPoints = vm.graphPoints.value
        assertThat(graphPoints).hasSize(1440)
        graphPoints.forEachIndexed { index, point ->
            assertThat(point.y).isEqualTo(0f)
            assertThat(point.x).isEqualTo(index.toFloat())
        }

        val daySummaryState = vm.daySummaryState.value
        assertThat(daySummaryState.date).isNull()
        assertThat(daySummaryState.loading).isFalse()
    }

    @Test
    fun `test proper group state when feedings have been fetched`() = runTest {
        // GIVEN: A newly initialized viewmodel
        val vm = FeedingListScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            reportGenerator = reportGenerator,
            dateConverter = dateConverter,
        )

        // WHEN: Viewmodel has been initialized with a list of Feedings
        scope.launch(dispatcherProvider.default) {
            vm.groupState.collect()
        }

        val numDays = 15
        val perDay = 3
        repo.emit(buildTestFeedings(numDays, perDay))

        // THEN: Group state should be initialized
        val groupState = vm.groupState.value

        assertThat(groupState.data).isNotEmpty()
        assertThat(groupState.data.keys).hasSize(numDays)
        assertThat(groupState.isLoading).isFalse()
    }

    @Test
    fun `test proper graph point state when feedings have been fetched`() = runTest {
        // GIVEN: A newly initialized viewmodel
        val vm = FeedingListScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            reportGenerator = reportGenerator,
            dateConverter = dateConverter,
        )

        // WHEN: Viewmodel has been initialized with a list of Feedings
        scope.launch(dispatcherProvider.default) {
            vm.graphPoints.collect()
        }

        repo.emit(buildFeedingsForToday())

        // THEN: Group and graph data should be initialized, day summay state unchanged
        val graphPoints = vm.graphPoints.value
        val nonZeroPoints = graphPoints.filter {
            it.y.toInt() > 0
        }
        assertThat(graphPoints).isNotEmpty()
        assertThat(nonZeroPoints).isNotEmpty()
    }

    @Test
    fun `test day summary was generated`() = runTest {
        // GIVEN: an initialized viewmodel with a list of feedings
        val vm = FeedingListScreenViewModel(
            repository = repo,
            dispatcherProvider = dispatcherProvider,
            preferenceManager = preferenceManager,
            reportGenerator = reportGenerator,
            dateConverter = dateConverter,
        )

        val summaryStates = mutableListOf<DaySummaryState>()

        scope.launch(dispatcherProvider.default) {
            vm.groupState.collect()
            vm.daySummaryState.collectLatest {
                summaryStates.add(it)
            }
        }

        val numDays = 15
        val perDay = 3
        repo.emit(buildTestFeedings(numDays, perDay))

        // WHEN: A report summary is requested to be generated
        val groupState = vm.groupState.value
        val keys = groupState.data.keys
        val date = keys.elementAt(0)

        vm.generateDaySummary(date, false, UnitOfMeasurement.MILLILITER) {

            // THEN: A report summary is returned
            assertThat(it).isNotNull()
            assertThat(it.isNotBlank()).isTrue()
        }
    }

    private fun buildTestFeedings(
        numDays: Int,
        perDay: Int,
    ): List<Feeding> {
        val year = 123
        val month = 11
        val day = 1
        val ret = mutableListOf<Feeding>()
        for (i in 1..numDays) {
            val x = Random.nextInt(0, 1)
            var quantity: Number = Random.nextInt(2, 5)
            var unit = UnitOfMeasurement.OUNCE
            if (x == 1) {
                quantity = Random.nextDouble(90.0, 180.0)
                unit = UnitOfMeasurement.MILLILITER
            }
            for (j in 1..perDay) {
                ret.add(
                    Feeding(
                        i.toLong(),
                        Date(year, month, day + i),
                        quantity.toDouble(),
                        unit,
                        "TEST NOTE $i",
                    ),
                )
            }
        }
        return ret
    }

    private fun buildFeedingsForToday(): List<Feeding> {
        val today = Date().apply {
            hours = 0
            minutes = 0
            seconds = 0
        }
        val ret = mutableListOf<Feeding>()
        for (i in 0 until 24) {
            val x = Random.nextInt(0, 1)
            var quantity: Number = Random.nextInt(2, 5)
            var unit = UnitOfMeasurement.OUNCE
            if (x == 1) {
                quantity = Random.nextDouble(90.0, 180.0)
                unit = UnitOfMeasurement.MILLILITER
            }
            ret.add(
                Feeding(
                    i.toLong(),
                    Date(today.time).apply {
                        hours = i
                    },
                    quantity.toDouble(),
                    unit,
                    "TEST NOTE $i",
                ),
            )
        }
        return ret
    }
}
