package com.nicholasfragiskatos.feedme.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeletedFeedingDao {
    @Query("SELECT * FROM deleted_feeding WHERE deleted_id = 1")
    fun getLastDeleted(): DeletedFeedingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveDeletedFeeding(deletedFeeding: DeletedFeedingEntity): Long
}