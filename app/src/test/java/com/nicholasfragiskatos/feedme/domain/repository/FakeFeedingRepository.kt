package com.nicholasfragiskatos.feedme.domain.repository

import com.nicholasfragiskatos.feedme.domain.model.Feeding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.Date

class FakeFeedingRepository : FeedingRepository {

    private val flow = MutableSharedFlow<List<Feeding>>()
    var feedingList = listOf<Feeding>()
    var feeding: Feeding? = null
    var savedFeeding: Feeding? = null

    suspend fun emit(value: List<Feeding>) = flow.emit(value)

    override suspend fun getFeedingById(id: Long): Feeding? = feeding

    override suspend fun saveFeeding(feeding: Feeding): Long {
        savedFeeding = feeding
        return feeding.id
    }

    override suspend fun deleteFeeding(feeding: Feeding) {}

    override fun getFeedings(): Flow<List<Feeding>> = flow

    override fun getFeedingsForToday(): Flow<List<Feeding>> = flow

    override suspend fun getFeedingsByDay(date: Date): List<Feeding> = feedingList
}
