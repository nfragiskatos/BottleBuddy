package com.nicholasfragiskatos.feedme.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [FeedingEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class FeedMeDatabase : RoomDatabase() {

    abstract fun feedingDao(): FeedingDao
}
