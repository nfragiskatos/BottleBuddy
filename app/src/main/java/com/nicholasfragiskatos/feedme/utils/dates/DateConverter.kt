package com.nicholasfragiskatos.feedme.utils.dates

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

interface DateConverter {
    fun convertToDate(ldt: LocalDateTime): Date

    fun convertToLocalDate(date: Date): LocalDate
}
