package com.nicholasfragiskatos.feedme.utils


import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement


object UnitUtils {
    const val MILLILITER_OZ_FACTOR = 29.574

    private const val decimalSeparator = '.'

    fun convertMeasurement(
        value: Double,
        unit: UnitOfMeasurement,
        preferredUnit: UnitOfMeasurement
    ): Double {
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

    // Only for non-negative numbers
    fun sanitizeDecimalInput(candidate: String, significantUnits: Int): String {

        if (candidate.matches("\\D".toRegex())) return ""
        val trimmed = candidate.replaceFirst("^0+(?!$)".toRegex(), "")

        val sb = StringBuilder()
        var hasDecimal = false
        var sigUnits = 0

        for (char in trimmed) {
            if (char.isDigit() && sigUnits < significantUnits) {
                sb.append(char)
                if (hasDecimal) {
                    ++sigUnits
                }
            } else if (char == decimalSeparator && !hasDecimal && sb.isNotEmpty()) {
                sb.append(char)
                hasDecimal = true
            }
        }

        return sb.toString()
    }
}
