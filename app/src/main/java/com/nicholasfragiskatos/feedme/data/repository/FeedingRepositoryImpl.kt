package com.nicholasfragiskatos.feedme.data.repository

import com.nicholasfragiskatos.feedme.data.local.FeedingDao
import com.nicholasfragiskatos.feedme.data.mapper.toFeeding
import com.nicholasfragiskatos.feedme.data.mapper.toFeedingEntity
import com.nicholasfragiskatos.feedme.domain.model.Feeding
import com.nicholasfragiskatos.feedme.domain.repository.FeedingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

class FeedingRepositoryImpl @Inject constructor(
    private val dao: FeedingDao,
) : FeedingRepository {
    override suspend fun getFeedingById(id: Long): Feeding? {
        return dao.getFeedingById(id)?.toFeeding()
    }

    override suspend fun saveFeeding(feeding: Feeding): Long {
        return dao.saveFeeding(feeding.toFeedingEntity())
    }

    override suspend fun deleteFeeding(feeding: Feeding) {
        dao.deleteFeeding(feeding.toFeedingEntity())
    }

    override fun getFeedings(): Flow<List<Feeding>> {
        return dao.getFeedings()
            .map { feedings -> feedings.map { feeding -> feeding.toFeeding() } }
    }

    override fun getFeedingsForToday(): Flow<List<Feeding>> {
        val localDate = LocalDate.now(ZoneId.systemDefault())
        val from = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val to =
            Date.from(localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
        return dao.getFeedingsByDateRange(from, to)
            .map { feedings -> feedings.map { feeding -> feeding.toFeeding() } }
    }
}
