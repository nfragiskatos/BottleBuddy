package com.nicholasfragiskatos.feedme.data.local

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun dateFromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}
