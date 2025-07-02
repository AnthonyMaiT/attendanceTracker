package edu.vt.mobiledev.attendancetracker

import android.app.Application
import edu.vt.mobiledev.dreamcatcher.database.AttendanceRepository

// intent application to initialize database
class AttendanceIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AttendanceRepository.initialize(this)
    }
}