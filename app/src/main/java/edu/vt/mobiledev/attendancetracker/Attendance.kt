package edu.vt.mobiledev.attendancetracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

// attendance entity
@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val date: Date = Date(),
)

// attendance record entity
@Entity(primaryKeys = ["attendanceId", "studentId"])
data class AttendanceRecord(
    val attendanceId: UUID,
    val studentId: UUID
)

// student entity
@Entity(tableName = "student")
data class Student(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val firstName: String = "",
    val lastName: String = "",
    val schoolId: String = "",
    val birthDate: Date = Date(),
) {
    val photoFileName get() = "IMG_$id.JPG"
}
