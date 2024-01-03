package com.nicholasfragiskatos.feedme.di

import com.nicholasfragiskatos.feedme.utils.DefaultDispatcherProvider
import com.nicholasfragiskatos.feedme.utils.DispatcherProvider
import com.nicholasfragiskatos.feedme.utils.PreferenceManager
import com.nicholasfragiskatos.feedme.utils.PreferenceManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {

    @Binds
    abstract fun bindDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider

    @Binds
    abstract fun bindPreferenceManager(preferenceManagerImpl: PreferenceManagerImpl) : PreferenceManager
}
