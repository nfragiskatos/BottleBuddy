package com.nicholasfragiskatos.feedme.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nicholasfragiskatos.feedme.BuildConfig
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    fun getPreferences() = dataStore.data.map {preferences ->
        val goal = try {
            (preferences[GOAL_KEY_DATA_STORE] ?: "0.0").toFloat()
        } catch (e: Exception) {
            0f
        }

        val goalUnit = preferences[UNIT_KEY_DATA_STORE]?.let {
            try {
                UnitOfMeasurement.valueOf(it)
            } catch (e: Exception) {
                UnitOfMeasurement.MILLILITER
            }
        } ?: UnitOfMeasurement.MILLILITER


        val displayUnit = preferences[PREFERRED_UNIT_KEY_DATA_STORE]?.let {
            try {
                UnitOfMeasurement.valueOf(it)
            } catch (e: Exception) {
                UnitOfMeasurement.MILLILITER
            }
        } ?: UnitOfMeasurement.MILLILITER

        FeedMePreferences(
            goal = goal,
            goalUnit = goalUnit,
            displayUnit = displayUnit
        )
    }

    fun <T> getData(key: Preferences.Key<T>, defaultValue: T) = dataStore.data.map { preferences ->
        preferences[key] ?: defaultValue
    }

    suspend fun <T> writeData(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    companion object {
        val GOAL_KEY_DATA_STORE = stringPreferencesKey("${BuildConfig.APPLICATION_ID}.GOAL_KEY")
        val UNIT_KEY_DATA_STORE = stringPreferencesKey("${BuildConfig.APPLICATION_ID}.UNIT_KEY")
        val PREFERRED_UNIT_KEY_DATA_STORE = stringPreferencesKey("${BuildConfig.APPLICATION_ID}.PREFERRED_UNIT_KEY")
    }
}