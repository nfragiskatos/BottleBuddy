package com.nicholasfragiskatos.feedme.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface FeedingDao {
    @Query("SELECT * FROM feedingentity")
    fun getAll(): List<FeedingEntity>
}