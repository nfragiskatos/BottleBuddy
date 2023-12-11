package com.nicholasfragiskatos.feedme.data.mapper

import com.nicholasfragiskatos.feedme.data.local.FeedingEntity
import com.nicholasfragiskatos.feedme.domain.Feeding

fun FeedingEntity.toFeeding(): Feeding {
    return Feeding(
        id = id,
        date = date,
        notes = notes,
        quantity = quantity,
        unit = unit,
    )
}

fun Feeding.toFeedingEntity(): FeedingEntity {
    return FeedingEntity(
        id = id,
        date = date,
        notes = notes,
        quantity = quantity,
        unit = unit,
    )
}
