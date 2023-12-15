package com.nicholasfragiskatos.feedme.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nicholasfragiskatos.feedme.BuildConfig
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    fun <T> getData(key: Preferences.Key<T>, defaultValue: T) = dataStore.data.map { preferences ->
        preferences[key] ?: defaultValue
    }

    suspend fun <T> writeData(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    companion object {
//        val Context.dataStore by preferencesDataStore(name = "FeedMePrefsDataStore")
        val GOAL_KEY_DATA_STORE = stringPreferencesKey("${BuildConfig.APPLICATION_ID}.GOAL_KEY")
        val UNIT_KEY_DATA_STORE = stringPreferencesKey("${BuildConfig.APPLICATION_ID}.UNIT_KEY")
    }
}