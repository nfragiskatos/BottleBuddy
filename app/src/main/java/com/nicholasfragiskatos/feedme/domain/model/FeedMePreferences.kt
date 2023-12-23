package com.nicholasfragiskatos.feedme.domain.model

data class FeedMePreferences(
    val goal: Float,
    val goalUnit: UnitOfMeasurement,
    val displayUnit: UnitOfMeasurement
)
