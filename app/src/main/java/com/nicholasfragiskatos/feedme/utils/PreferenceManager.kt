package com.nicholasfragiskatos.feedme.utils

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nicholasfragiskatos.feedme.BuildConfig
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import kotlinx.coroutines.flow.Flow

interface PreferenceManager {
    fun getPreferences(): Flow<FeedMePreferences>
    fun <T> getData(key: Preferences.Key<T>, defaultValue: T): Flow<T>

    suspend fun <T> writeData(key: Preferences.Key<T>, value: T)

    companion object {
        val GOAL_KEY_DATA_STORE = stringPreferencesKey("${BuildConfig.APPLICATION_ID}.GOAL_KEY")
        val UNIT_KEY_DATA_STORE = stringPreferencesKey("${BuildConfig.APPLICATION_ID}.UNIT_KEY")
        val PREFERRED_UNIT_KEY_DATA_STORE = stringPreferencesKey("${BuildConfig.APPLICATION_ID}.PREFERRED_UNIT_KEY")
    }
}