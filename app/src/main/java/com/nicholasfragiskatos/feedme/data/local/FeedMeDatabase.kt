package com.nicholasfragiskatos.feedme.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FeedingEntity::class, DeletedFeedingEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FeedMeDatabase : RoomDatabase() {

    abstract fun feedingDao(): FeedingDao

    abstract fun deletedFeedingDao(): DeletedFeedingDao

    companion object {
        const val DATABASE_NAME = "feed_me_db"
    }
}
