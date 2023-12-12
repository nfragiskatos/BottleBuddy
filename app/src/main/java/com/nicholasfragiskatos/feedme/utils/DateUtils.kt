package com.nicholasfragiskatos.feedme.utils

import android.icu.util.Calendar
import android.icu.util.TimeZone
import java.util.Date

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
