package com.nicholasfragiskatos.feedme.data.repository

import com.nicholasfragiskatos.feedme.data.local.DeletedFeedingDao
import com.nicholasfragiskatos.feedme.data.local.FeedingDao
import com.nicholasfragiskatos.feedme.data.mapper.toDeletedFeedingEntity
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
    private val feedingDao: FeedingDao,
    private val deletedFeedingDao: DeletedFeedingDao
) : FeedingRepository {
    override suspend fun getFeedingById(id: Long): Feeding? {
        return feedingDao.getFeedingById(id)?.toFeeding()
    }

    override suspend fun saveFeeding(feeding: Feeding): Long {
        return feedingDao.saveFeeding(feeding.toFeedingEntity())
    }

    override suspend fun deleteFeeding(feeding: Feeding) {
        val toDelete = feedingDao.getFeedingById(feeding.id)
        toDelete?.let {
            deletedFeedingDao.saveDeletedFeeding(toDelete.toDeletedFeedingEntity())
            feedingDao.deleteFeeding(toDelete)
        }
    }

    override fun getFeedings(): Flow<List<Feeding>> {
        return feedingDao.getFeedings()
            .map { feedings -> feedings.map { feeding -> feeding.toFeeding() } }
    }

    override fun getFeedingsForToday(): Flow<List<Feeding>> {
        val localDate = LocalDate.now(ZoneId.systemDefault())
        val from = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val to =
            Date.from(localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
        return feedingDao.getFeedingsListFlowByDateRange(from, to)
            .map { feedings -> feedings.map { feeding -> feeding.toFeeding() } }
    }

    override suspend fun getFeedingsByDay(date: Date): List<Feeding> {
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val from = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val to =
            Date.from(localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
        return feedingDao.getFeedingsListByDateRange(from, to).map { it.toFeeding() }
    }

    override suspend fun undoLastDelete(): Long {
        val lastDeleted = deletedFeedingDao.getLastDeleted()
        lastDeleted?.let {
            feedingDao.saveFeeding(it.toFeedingEntity())
        }
        return lastDeleted?.id ?: 0L
    }
}
