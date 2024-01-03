package com.nicholasfragiskatos.feedme.di

import com.nicholasfragiskatos.feedme.utils.DefaultDispatcherProvider
import com.nicholasfragiskatos.feedme.utils.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DispatcherModule {

    @Binds
    abstract fun bindDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider
}
