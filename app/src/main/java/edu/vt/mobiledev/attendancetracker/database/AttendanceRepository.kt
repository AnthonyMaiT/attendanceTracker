package edu.vt.mobiledev.attendancetracker.database

import android.content.Context
import androidx.room.Room
import edu.vt.mobiledev.attendancetracker.Attendance
import edu.vt.mobiledev.attendancetracker.AttendanceRecord
import edu.vt.mobiledev.attendancetracker.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlinx.coroutines.launch

private const val DATABASE_NAME = "attendance-database"

class AttendanceRepository constructor(context: Context, private val coroutineScope: CoroutineScope = GlobalScope) {


    // adds attendance record
    suspend fun addAttendanceRecord(attendanceRecord: AttendanceRecord) {
        database.attendanceDao().insertAttendanceRecord(attendanceRecord)
    }

    // gets a attendance record
    suspend fun getAttendanceRecord(attendanceRecord: AttendanceRecord): AttendanceRecord {
        return database.attendanceDao().getAttendanceRecord(attendanceRecord.studentId, attendanceRecord.attendanceId)
    }

    // deletes attendance record
    fun deleteAttendanceRecord(attendanceId: UUID, studentId: UUID) {
        coroutineScope.launch {
            database.attendanceDao().deleteAttendanceRecord(attendanceId, studentId)
        }
    }

    // returns attendance days
    fun getAttendanceDays(): Flow<List<Attendance>> {
        return database.attendanceDao().getAttendanceDays()
    }

    // gets students for attendance day
    fun getStudentsForAttendance(attendanceId: UUID): Flow<List<Student>> {
        return database.attendanceDao().getStudentsForAttendance(attendanceId)
    }


    // gets attendance
    suspend fun getAttendance(id: UUID): Attendance {
        return database.attendanceDao().getAttendance(id)
    }

    // updates attendance
    fun updateAttendance(attendance: Attendance) {
        coroutineScope.launch {
            database.attendanceDao().updateAttendance(attendance)
        }
    }

    // adds attendance
    suspend fun addAttendance(attendance: Attendance) {
        database.attendanceDao().insertAttendance(attendance)
    }

    // delete attendance
    fun deleteAttendance(attendance: Attendance) {
        coroutineScope.launch {
            database.attendanceDao().deleteAttendance(attendance)
        }
    }

    // get counts of students for all attendance days
    suspend fun getStudentsForAttendanceCount(): List<Int> {
        return database.attendanceDao().getStudentsForAttendanceCount()
    }

    // get all students
    fun getStudents(): Flow<List<Student>> {
        return database.attendanceDao().getStudents()
    }

    // gets student by school id
    suspend fun getStudentBySchoolId(id: String): List<Student> {
        val dre = database.attendanceDao().getStudentBySchoolId(id)
        return dre
    }

    // gets student
    suspend fun getStudent(id: UUID): Student {
        val dre = database.attendanceDao().getStudent(id)
        return dre
    }

    // updates student
    fun updateStudent(student: Student) {
        coroutineScope.launch {
            database.attendanceDao().updateStudent(student)
        }
    }

    // adds student
    suspend fun addStudent(student: Student) {
        database.attendanceDao().insertStudent(student)
    }

    // deletes student
    fun deleteStudent(student: Student) {
        coroutineScope.launch {
            database.attendanceDao().deleteStudent(student)
        }
    }

    // data base object
    private val database: AttendanceDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            AttendanceDatabase::class.java,
            DATABASE_NAME
        )
        .build()

    // initialize the repo
    companion object {
        private var INSTANCE: AttendanceRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = AttendanceRepository(context)
            }
        }

        fun get(): AttendanceRepository {
            return INSTANCE ?:
            throw IllegalStateException("AttendanceRepository must be initialized")
        }
    }
}