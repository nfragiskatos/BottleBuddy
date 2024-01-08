package com.nicholasfragiskatos.feedme.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import java.util.Date

@Entity(tableName = "deleted_feeding")
data class DeletedFeedingEntity(
    val id: Long,
    val date: Date,
    val quantity: Double,
    val unit: UnitOfMeasurement,
    val notes: String,
    @ColumnInfo(name = "date_deleted")
    val dateDeleted: Date = Date(),
) {
    // This needs to be visible and settable for Room...
    // Only keeping one entry in this table, so any new entries
    // will have the same id, and overwrite the previous one.
    @PrimaryKey
    @ColumnInfo(name = "deleted_id")
    var deletedId: Long = 1
        set(value) {}
}
