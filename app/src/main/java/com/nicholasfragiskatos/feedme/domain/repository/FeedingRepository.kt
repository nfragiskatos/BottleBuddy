package com.nicholasfragiskatos.feedme.domain.repository

import com.nicholasfragiskatos.feedme.domain.model.Feeding

interface FeedingRepository {

    suspend fun getFeedingById(id: Int): Feeding?

    suspend fun saveFeeding(feeding: Feeding): Long
}
