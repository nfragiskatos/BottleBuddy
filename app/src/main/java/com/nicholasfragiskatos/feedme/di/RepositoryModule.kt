package com.nicholasfragiskatos.feedme.di

import com.nicholasfragiskatos.feedme.data.repository.FeedingRepositoryImpl
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindFeedingRepository(impl: FeedingRepositoryImpl): FeedingRepository
}
