package com.nicholasfragiskatos.feedme.domain.model

import java.util.Date

data class Feeding(
    val id: Int = 0,
    val date: Date = Date(),
    val quantity: Double = 0.0,
    val unit: UnitOfMeasurement = UnitOfMeasurement.MILLILITER,
    val notes: String = ""
)
