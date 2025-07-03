package edu.vt.mobiledev.attendancetracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.vt.mobiledev.attendancetracker.Attendance
import edu.vt.mobiledev.attendancetracker.AttendanceRecord
import edu.vt.mobiledev.attendancetracker.Student

// Database class calling DAO
@Database(entities = [Attendance::class, Student::class, AttendanceRecord::class], version = 1, exportSchema = false)
@TypeConverters(AttendanceTypeConverters::class)
abstract class AttendanceDatabase: RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
}