package com.nicholasfragiskatos.feedme.utils.preferences

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
        val goalUnit = preferences[PreferenceManager.UNIT_KEY_DATA_STORE]?.let {
            try {
                UnitOfMeasurement.valueOf(it)
            } catch (e: Exception) {
                UnitOfMeasurement.MILLILITER
            }
        } ?: UnitOfMeasurement.MILLILITER

        var goalStr = try {
            preferences[PreferenceManager.GOAL_KEY_DATA_STORE]
        } catch (e: Exception) {
            null
        }

        if (goalStr.isNullOrBlank()) {
            goalStr = if (goalUnit == UnitOfMeasurement.MILLILITER) "500.0" else "15.0"
        }

        val goal = try {
            goalStr.toFloat()
        } catch (e: Exception) {
            if (goalUnit == UnitOfMeasurement.MILLILITER) 500.0f else 15.0f
        }

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