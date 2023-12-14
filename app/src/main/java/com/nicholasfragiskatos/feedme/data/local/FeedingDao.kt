package com.nicholasfragiskatos.feedme.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingDao {
    @Query("SELECT * FROM feedingentity ORDER BY date DESC")
    fun getFeedings(): Flow<List<FeedingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFeeding(feeding: FeedingEntity): Long

    @Query("SELECT * FROM feedingentity WHERE id = :id")
    suspend fun getFeedingById(id: Long): FeedingEntity?

    @Delete
    suspend fun deleteFeeding(feeding: FeedingEntity)
}
