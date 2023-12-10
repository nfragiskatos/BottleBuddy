package com.nicholasfragiskatos.feedme.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class FeedingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: Date = Date(),
    val quantity: Double,
    val unit: String,
    val notes: String
)
