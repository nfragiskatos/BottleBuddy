package com.nicholasfragiskatos.feedme.utils.dates

import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

object DateFormatter {

    private val dateFormatDate = DateFormat.getPatternInstance("yMMMdE")
    private val dateFormatTime = DateFormat.getTimeInstance(DateFormat.SHORT)
    private val dateFormatTime24 = DateFormat.getPatternInstance("Hm")
    private val dateFormatDateTime = DateFormat.getPatternInstance("yMMMdEjm")
    private val dateFormatDateTime24 = DateFormat.getPatternInstance("yMMMdEHm")

    // M3 DatePicker returns dates in UTC, and you can't change it, so that's why this is here.
    // Otherwise the date when displaying it in local time would be off. For example here in
    // MST, it's UTC-7
    fun convertUtcToLocalDate(utcMilliseconds: Long): Date {
        val selectedUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        selectedUtc.timeInMillis = utcMilliseconds
        val selectedLocal = Calendar.getInstance()
        selectedLocal.clear()
        selectedLocal.set(
            selectedUtc.get(Calendar.YEAR),
            selectedUtc.get(Calendar.MONTH),
            selectedUtc.get(Calendar.DATE),
        )
        return selectedLocal.time
    }

    fun getFormattedDate(date: Date): String = dateFormatDate.format(date)

    fun getFormattedTime(date: Date, is24HourFormat: Boolean = false): String =
        if (is24HourFormat) dateFormatTime24.format(date) else dateFormatTime.format(date)

    fun getFormattedTime(date: LocalDateTime, is24HourFormat: Boolean = false): String =
        getFormattedTime(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()), is24HourFormat)

    fun getFormattedDate(date: LocalDateTime): String =
        getFormattedDate(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()))

    fun getFormattedDateWithTime(date: Date, is24HourFormat: Boolean = false): String =
        if (is24HourFormat) dateFormatDateTime24.format(date) else dateFormatDateTime.format(date)
}
