package com.nicholasfragiskatos.feedme.utils

import androidx.datastore.preferences.core.Preferences
import com.nicholasfragiskatos.feedme.domain.model.FeedMePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakePreferenceManager : PreferenceManager {
    private val preferenceFlow = MutableSharedFlow<FeedMePreferences>()
    private val dataFlow = MutableSharedFlow<Any>(replay = 1)

    suspend fun emitFromPreferenceFlow(value: FeedMePreferences) = preferenceFlow.emit(value)
    suspend fun emitFromDataFlow(value: Any) = dataFlow.emit(value)
    override fun getPreferences(): Flow<FeedMePreferences> = preferenceFlow

    override fun <T> getData(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataFlow as Flow<T>

    override suspend fun <T> writeData(key: Preferences.Key<T>, value: T) {}
}
