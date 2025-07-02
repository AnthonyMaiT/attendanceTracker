package edu.vt.mobiledev.dreamcatcher.database

import androidx.room.TypeConverter
import java.util.Date

// Used to convert date type
class AttendanceTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}