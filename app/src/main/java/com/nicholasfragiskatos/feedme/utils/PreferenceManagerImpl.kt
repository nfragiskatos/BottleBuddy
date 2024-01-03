package com.nicholasfragiskatos.feedme.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceManager {

    override fun getPreferences() = dataStore.data.map { preferences ->
        val goal = try {
            (preferences[PreferenceManager.GOAL_KEY_DATA_STORE] ?: "0.0").toFloat()
        } catch (e: Exception) {
            0f
        }

        val goalUnit = preferences[PreferenceManager.UNIT_KEY_DATA_STORE]?.let {
            try {
                UnitOfMeasurement.valueOf(it)
            } catch (e: Exception) {
                UnitOfMeasurement.MILLILITER
            }
        } ?: UnitOfMeasurement.MILLILITER


        val displayUnit = preferences[PreferenceManager.PREFERRED_UNIT_KEY_DATA_STORE]?.let {
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

    override fun <T> getData(key: Preferences.Key<T>, defaultValue: T) = dataStore.data.map { preferences ->
        preferences[key] ?: defaultValue
    }

    override suspend fun <T> writeData(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}