package com.nicholasfragiskatos.feedme.utils.preferences

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.runners.JUnit4
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
@HiltAndroidTest
class PreferenceManagerImplTest {

    private val scope = TestScope()
    private var job: Job? = null
//    private lateinit var context: Context

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        context = InstrumentationRegistry.getInstrumentation().context
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        File(context.filesDir, "datastore").deleteRecursively()
        job?.cancel()
    }

    @Test
    fun preferenceManager_readForNonExistingDataStore() = runTest {
        val defVal = "DEFAULT GOAL"

        val items = mutableListOf<String>()
        job = scope.launch {
            preferenceManager.getData(PreferenceManager.GOAL_KEY_DATA_STORE, defVal).toList(items)
        }
        scope.advanceUntilIdle()

        assertThat(items).isEmpty()
    }


    @Test
    fun preferenceManager_writeAndReadForExistingGoalPreference() = runTest {
        val prefVal = "TEST GOAL"
        val defVal = "DEFAULT GOAL"
        preferenceManager.writeData(PreferenceManager.GOAL_KEY_DATA_STORE, prefVal)


        val items = mutableListOf<String>()
        job = scope.launch {
            preferenceManager.getData(PreferenceManager.GOAL_KEY_DATA_STORE, defVal).toList(items)
        }
        scope.advanceUntilIdle()

        assertThat(items).isNotEmpty()
        assertThat(items).hasSize(1)
        assertThat(items[0]).isEqualTo(prefVal)
    }

    @Test
    fun preferenceManager_writeAndReadForNonExistingGoalPreference() = runTest {
        val prefVal = "TEST GOAL"
        val defVal = "DEFAULT GOAL"
        preferenceManager.writeData(stringPreferencesKey("TEST_KEY"), prefVal)


        val items = mutableListOf<String>()
        job = scope.launch {
            preferenceManager.getData(PreferenceManager.GOAL_KEY_DATA_STORE, defVal).toList(items)
        }
        scope.advanceUntilIdle()

        assertThat(items).isNotEmpty()
        assertThat(items).hasSize(1)
        assertThat(items[0]).isEqualTo(defVal)
    }

    @Test
    fun preferenceManager_writeAndReadForExistingUnitPreference() = runTest {
        val prefVal = "TEST UNIT"
        val defVal = "DEFAULT UNIT"
        preferenceManager.writeData(PreferenceManager.UNIT_KEY_DATA_STORE, prefVal)


        val items = mutableListOf<String>()
        job = scope.launch {
            preferenceManager.getData(PreferenceManager.UNIT_KEY_DATA_STORE, defVal).toList(items)
        }
        scope.advanceUntilIdle()

        assertThat(items).isNotEmpty()
        assertThat(items).hasSize(1)
        assertThat(items[0]).isEqualTo(prefVal)
    }

    @Test
    fun preferenceManager_writeAndReadForNonExistingUnitPreference() = runTest {
        val prefVal = "TEST UNIT"
        val defVal = "DEFAULT UNIT"
        preferenceManager.writeData(stringPreferencesKey("TEST_KEY"), prefVal)


        val items = mutableListOf<String>()
        job = scope.launch {
            preferenceManager.getData(PreferenceManager.UNIT_KEY_DATA_STORE, defVal).toList(items)
        }
        scope.advanceUntilIdle()

        assertThat(items).isNotEmpty()
        assertThat(items).hasSize(1)
        assertThat(items[0]).isEqualTo(defVal)
    }

    @Test
    fun preferenceManager_writeAndReadForExistingDisplayUnitPreference() = runTest {
        val prefVal = "TEST UNIT"
        val defVal = "DEFAULT UNIT"
        preferenceManager.writeData(PreferenceManager.PREFERRED_UNIT_KEY_DATA_STORE, prefVal)


        val items = mutableListOf<String>()
        job = scope.launch {
            preferenceManager.getData(PreferenceManager.PREFERRED_UNIT_KEY_DATA_STORE, defVal).toList(items)
        }
        scope.advanceUntilIdle()

        assertThat(items).isNotEmpty()
        assertThat(items).hasSize(1)
        assertThat(items[0]).isEqualTo(prefVal)
    }

    @Test
    fun preferenceManager_writeAndReadForNonExistingDisplayUnitPreference() = runTest {
        val prefVal = "TEST UNIT"
        val defVal = "DEFAULT GOAL"
        preferenceManager.writeData(stringPreferencesKey("TEST_KEY"), prefVal)


        val items = mutableListOf<String>()
        job = scope.launch {
            preferenceManager.getData(PreferenceManager.PREFERRED_UNIT_KEY_DATA_STORE, defVal).toList(items)
        }
        scope.advanceUntilIdle()

        assertThat(items).isNotEmpty()
        assertThat(items).hasSize(1)
        assertThat(items[0]).isEqualTo(defVal)
    }


    @Test
    fun preferenceManager_getPreferences() = runTest {
        val prefGoalVal = 56.5f
        val prefGoalUnit = UnitOfMeasurement.MILLILITER
        val prefDisplayUnit = UnitOfMeasurement.OUNCE
        preferenceManager.writeData(PreferenceManager.GOAL_KEY_DATA_STORE, prefGoalVal.toString())
        preferenceManager.writeData(PreferenceManager.UNIT_KEY_DATA_STORE, prefGoalUnit.name)
        preferenceManager.writeData(PreferenceManager.PREFERRED_UNIT_KEY_DATA_STORE, prefDisplayUnit.name)


        val items = mutableListOf<FeedMePreferences>()
        job = scope.launch {
            preferenceManager.getPreferences().toList(items)
        }
        scope.advanceUntilIdle()


        assertThat(items).isNotEmpty()
        assertThat(items).hasSize(1)
        val prefs = items[0]
        assertThat(prefs.goal).isEqualTo(prefGoalVal)
        assertThat(prefs.goalUnit).isEqualTo(prefGoalUnit)
        assertThat(prefs.displayUnit).isEqualTo(prefDisplayUnit)
    }
}