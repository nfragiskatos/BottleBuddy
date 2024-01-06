package com.nicholasfragiskatos.feedme.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton
import kotlin.random.Random

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SystemModule::class]
)
object TestSystemModule {

    @Singleton
    @Provides
    fun provideFakePreferences(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        val random = Random.nextInt() // generating here
        return PreferenceDataStoreFactory
            .create(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = {
                    // creating a new file for every test case and finally
                    // deleting them all
                    context.preferencesDataStoreFile("test_pref_file-$random")
                }
            )
    }
}