package com.nicholasfragiskatos.feedme.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

class DateConverterImpl @Inject constructor() : DateConverter {
    override fun convertToDate(ldt: LocalDateTime): Date {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
    }

    override fun convertToLocalDate(date: Date): LocalDate {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
}
