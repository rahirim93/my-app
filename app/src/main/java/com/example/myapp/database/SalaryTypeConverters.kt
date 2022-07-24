package com.example.myapp.database

import androidx.room.TypeConverter
import java.util.*

class SalaryTypeConverters {

    @TypeConverter
    fun fromDate(date: Calendar?): Long? {
        return date?.timeInMillis
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Calendar? {
        val calendar = Calendar.getInstance()
        if (millisSinceEpoch != null) {
            calendar.timeInMillis = millisSinceEpoch
        }
        return calendar
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}