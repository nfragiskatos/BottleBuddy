package com.nicholasfragiskatos.feedme.domain.repository

import com.nicholasfragiskatos.feedme.domain.model.Feeding
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface FeedingRepository {

    suspend fun getFeedingById(id: Long): Feeding?

    suspend fun saveFeeding(feeding: Feeding): Long

    suspend fun deleteFeeding(feeding: Feeding)

    fun getFeedings(): Flow<List<Feeding>>

    fun getFeedingsForToday(): Flow<List<Feeding>>

    suspend fun getFeedingsByDay(date: Date) : List<Feeding>
}
