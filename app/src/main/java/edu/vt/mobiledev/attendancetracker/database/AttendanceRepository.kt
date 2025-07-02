package edu.vt.mobiledev.dreamcatcher.database

import android.content.Context
import androidx.room.Room
import edu.vt.mobiledev.attendancetracker.Attendance
import edu.vt.mobiledev.attendancetracker.AttendanceRecord
import edu.vt.mobiledev.attendancetracker.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val DATABASE_NAME = "attendance-database"

class AttendanceRepository constructor(context: Context, private val coroutineScope: CoroutineScope = GlobalScope) {

    // Transform the DAO multimap into a list of dreams with their entries:
    fun getAttendanceDays(): Flow<List<Attendance>> {
        val dreamMultiMap = database.attendanceDao().getAttendanceDays()
        return dreamMultiMap
    }

    fun getStudentsForAttendance(attendanceId: UUID): Flow<List<Student>> {
        val dreamMultiMap = database.attendanceDao().getStudentsForAttendance(attendanceId)
        return dreamMultiMap
    }

    // Call the DAO transaction function, to get the dream and its entries:
    suspend fun getAttendance(id: UUID): Attendance {
        val dre = database.attendanceDao().getAttendance(id)
        return dre
    }

    suspend fun getStudentBySchoolId(id: String): List<Student> {
        val dre = database.attendanceDao().getStudentBySchoolId(id)
        return dre
    }

    fun updateAttendance(attendance: Attendance) {
        coroutineScope.launch {
            database.attendanceDao().updateAttendance(attendance)
        }
    }

    suspend fun addAttendance(attendance: Attendance) {
        database.attendanceDao().insertAttendance(attendance)
    }

    suspend fun addAttendanceRecord(attendanceRecord: AttendanceRecord) {
        database.attendanceDao().insertAttendanceRecord(attendanceRecord)
    }

    suspend fun getAttendanceRecord(attendanceRecord: AttendanceRecord): AttendanceRecord {
        return database.attendanceDao().getAttendanceRecord(attendanceRecord.studentId, attendanceRecord.attendanceId)
    }

    fun deleteAttendance(attendance: Attendance) {
        coroutineScope.launch {
            database.attendanceDao().deleteAttendance(attendance)
        }
    }

    fun deleteStudentFromAttendance(student: Student) {
        coroutineScope.launch {
            database.attendanceDao().deleteStudentFromAttendance(student)
        }
    }

    fun getStudents(): Flow<List<Student>> {
        val dreamMultiMap = database.attendanceDao().getStudents()
        return dreamMultiMap
    }

    // Call the DAO transaction function, to get the dream and its entries:
    suspend fun getStudent(id: UUID): Student {
        val dre = database.attendanceDao().getStudent(id)
        return dre
    }

    // Listing 13.25:
    // called to update dream in database
    fun updateStudent(student: Student) {
        coroutineScope.launch {
            database.attendanceDao().updateStudent(student)
        }
    }

    suspend fun addStudent(student: Student) {
        database.attendanceDao().insertStudent(student)
    }

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