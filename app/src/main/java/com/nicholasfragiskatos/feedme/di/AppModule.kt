package com.nicholasfragiskatos.feedme.di

import android.app.Application
import androidx.room.Room
import com.nicholasfragiskatos.feedme.data.local.FeedMeDatabase
import com.nicholasfragiskatos.feedme.data.repository.FeedingRepositoryImpl
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesFeedMeDatabase(app: Application): FeedMeDatabase {
        return Room.databaseBuilder(
            app,
            FeedMeDatabase::class.java,
            FeedMeDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providesFeedingRepository(db: FeedMeDatabase): FeedingRepository {
        return FeedingRepositoryImpl(db.feedingDao())
    }
}