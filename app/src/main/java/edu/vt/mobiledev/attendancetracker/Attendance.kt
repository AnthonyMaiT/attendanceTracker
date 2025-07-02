package edu.vt.mobiledev.attendancetracker

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date
import java.util.UUID

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val date: Date = Date(),
)

@Entity(primaryKeys = ["attendanceId", "studentId"])
data class AttendanceRecord(
    val attendanceId: UUID,
    val studentId: UUID
)

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
