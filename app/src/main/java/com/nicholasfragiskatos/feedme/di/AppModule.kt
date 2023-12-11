package com.nicholasfragiskatos.feedme.di

import android.app.Application
import androidx.room.Room
import com.nicholasfragiskatos.feedme.data.local.FeedMeDatabase
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
}