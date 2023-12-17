package com.nicholasfragiskatos.feedme.utils

import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement



object UnitUtils {

    const val MILLILITER_OZ_FACTOR = 29.574
    fun convertMeasurement(value: Double, unit: UnitOfMeasurement, preferredUnit: UnitOfMeasurement) : Double {
        if (unit == preferredUnit) {
            return value
        }

        return when (preferredUnit) {
            UnitOfMeasurement.OUNCE -> {
                value / MILLILITER_OZ_FACTOR
            }
            UnitOfMeasurement.MILLILITER -> {
                value * MILLILITER_OZ_FACTOR
            }
        }
    }
}
