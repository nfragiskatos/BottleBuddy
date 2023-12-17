package com.nicholasfragiskatos.feedme.utils

import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import com.nicholasfragiskatos.feedme.utils.UnitUtils.MILLILITER_OZ_FACTOR
import com.nicholasfragiskatos.feedme.utils.UnitUtils.convertMeasurement
import com.nicholasfragiskatos.feedme.utils.UnitUtils.sanitizeDecimalInput
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.math.abs
import kotlin.math.max

//fun Double.equalsDelta(other: Double) = Math.abs(this - other) < 0.00001 * Math.max(Math.abs(this), Math.abs(other))
fun Double.equalsDelta(other: Double) = abs(this - other) < max(Math.ulp(this), Math.ulp(other)) * 2

class UnitUtilsTest {

    @Test
    fun `convert milliliters to ounces`() {

        var mil = 0.0
        var expected = mil / MILLILITER_OZ_FACTOR
        var isEqual = expected.equalsDelta(convertMeasurement(mil, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.OUNCE))
        assertTrue(isEqual)

        mil = 1.0
        expected = mil / MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(mil, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.OUNCE))
        assertTrue(isEqual)

        mil = 25.5
        expected = mil / MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(mil, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.OUNCE))
        assertTrue(isEqual)

        mil = 300.5
        expected = mil / MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(mil, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.OUNCE))
        assertTrue(isEqual)

        mil = 1012.67
        expected = mil / MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(mil, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.OUNCE))
        assertTrue(isEqual)

        mil = 87.06
        expected = mil / MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(mil, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.OUNCE))
        assertTrue(isEqual)
    }

    @Test
    fun `convert ounces to milliliters`() {

        var ounces = 0.0
        var expected = ounces * MILLILITER_OZ_FACTOR
        var isEqual = expected.equalsDelta(convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.MILLILITER))
        assertTrue(isEqual)

        ounces = 1.0
        expected = ounces * MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.MILLILITER))
        assertTrue(isEqual)

        ounces = 25.5
        expected = ounces * MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.MILLILITER))
        assertTrue(isEqual)

        ounces = 300.5
        expected = ounces * MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.MILLILITER))
        assertTrue(isEqual)

        ounces = 1012.67
        expected = ounces * MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.MILLILITER))
        assertTrue(isEqual)

        ounces = 87.06
        expected = ounces * MILLILITER_OZ_FACTOR
        isEqual = expected.equalsDelta(convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.MILLILITER))
        assertTrue(isEqual)
    }

    @Test
    fun `convert ounces to ounces`() {
        var ounces = 0.0
        var conversion = convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.OUNCE)
        assertEquals(ounces, conversion)

        ounces = 1.0
        conversion = convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.OUNCE)
        assertEquals(ounces, conversion)

        ounces = 25.5
        conversion = convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.OUNCE)
        assertEquals(ounces, conversion)

        ounces = 300.5
        conversion = convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.OUNCE)
        assertEquals(ounces, conversion)

        ounces = 1012.67
        conversion = convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.OUNCE)
        assertEquals(ounces, conversion)

        ounces = 87.06
        conversion = convertMeasurement(ounces, UnitOfMeasurement.OUNCE, UnitOfMeasurement.OUNCE)
        assertEquals(ounces, conversion)
    }

    @Test
    fun `convert milliliters to milliliters`() {
        var ounces = 0.0
        var conversion = convertMeasurement(ounces, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.MILLILITER)
        assertEquals(ounces, conversion)

        ounces = 1.0
        conversion = convertMeasurement(ounces, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.MILLILITER)
        assertEquals(ounces, conversion)

        ounces = 25.5
        conversion = convertMeasurement(ounces, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.MILLILITER)
        assertEquals(ounces, conversion)

        ounces = 300.5
        conversion = convertMeasurement(ounces, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.MILLILITER)
        assertEquals(ounces, conversion)

        ounces = 1012.67
        conversion = convertMeasurement(ounces, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.MILLILITER)
        assertEquals(ounces, conversion)

        ounces = 87.06
        conversion = convertMeasurement(ounces, UnitOfMeasurement.MILLILITER, UnitOfMeasurement.MILLILITER)
        assertEquals(ounces, conversion)
    }

    @Test
    fun `sanitize decimal input`() {
        var candidate = "1"
        var signUnits = 2
        assertEquals("1", sanitizeDecimalInput(candidate, signUnits))

        candidate = "01"
        signUnits = 2
        assertEquals("1", sanitizeDecimalInput(candidate, signUnits))

        candidate = "001"
        signUnits = 2
        assertEquals("1", sanitizeDecimalInput(candidate, signUnits))

        candidate = "1.1"
        signUnits = 2
        assertEquals("1.1", sanitizeDecimalInput(candidate, signUnits))

        candidate = "1.12"
        signUnits = 2
        assertEquals("1.12", sanitizeDecimalInput(candidate, signUnits))

        candidate = "1.123"
        signUnits = 2
        assertEquals("1.12", sanitizeDecimalInput(candidate, signUnits))

        candidate = "00001.12345"
        signUnits = 2
        assertEquals("1.12", sanitizeDecimalInput(candidate, signUnits))

        candidate = "00001.1.2345"
        signUnits = 4
        assertEquals("1.1234", sanitizeDecimalInput(candidate, signUnits))

        candidate = "00001.12...345"
        signUnits = 5
        assertEquals("1.12345", sanitizeDecimalInput(candidate, signUnits))
    }
}