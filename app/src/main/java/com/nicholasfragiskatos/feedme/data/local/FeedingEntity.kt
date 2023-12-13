package com.nicholasfragiskatos.feedme.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import java.util.Date

@Entity
data class FeedingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Date = Date(),
    val quantity: Double,
    val unit: UnitOfMeasurement,
    val notes: String
)
