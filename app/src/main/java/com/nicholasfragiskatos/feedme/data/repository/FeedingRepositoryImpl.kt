package com.nicholasfragiskatos.feedme.data.repository

import com.nicholasfragiskatos.feedme.data.local.FeedingDao
import com.nicholasfragiskatos.feedme.data.mapper.toFeeding
import com.nicholasfragiskatos.feedme.data.mapper.toFeedingEntity
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository

class FeedingRepositoryImpl(
    private val dao: FeedingDao,
) : FeedingRepository {
    override suspend fun getFeedingById(id: Int): Feeding? {
        return dao.getFeedingById(id)?.toFeeding()
    }

    override suspend fun saveFeeding(feeding: Feeding): Long {
        return dao.saveFeeding(feeding.toFeedingEntity())
    }
}
