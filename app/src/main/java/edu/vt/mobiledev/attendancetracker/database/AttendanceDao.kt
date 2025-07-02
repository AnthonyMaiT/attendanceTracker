package edu.vt.mobiledev.dreamcatcher.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import edu.vt.mobiledev.attendancetracker.Attendance
import edu.vt.mobiledev.attendancetracker.AttendanceRecord
import edu.vt.mobiledev.attendancetracker.Student
import kotlinx.coroutines.flow.Flow
import java.util.UUID

// Data access object for dream model
@Dao
interface AttendanceDao {
    @Query("""
        SELECT s.*
        FROM student s
        INNER JOIN attendanceRecord ar ON s.id = ar.studentId
        WHERE ar.attendanceId = :attendanceId
    """)
    fun getStudentsForAttendance(attendanceId: UUID): Flow<List<Student>>

    @Query("""
        SELECT COUNT(ar.studentId)
        FROM attendance a
        LEFT JOIN attendanceRecord ar ON a.id = ar.attendanceId
        GROUP BY a.id
        ORDER BY a.date DESC
    """)
    suspend fun getStudentsForAttendanceCount(): List<Int>

    @Query("DELETE FROM attendanceRecord WHERE studentId = (:studentId)  ")
    suspend fun internalDeleteStudentFromAttendance(studentId: UUID)

    @Query("DELETE FROM attendanceRecord WHERE attendanceId = (:attendanceId)  ")
    suspend fun internalDeleteAttendanceRecordByAttendance(attendanceId: UUID)

    @Transaction
    suspend fun deleteStudentFromAttendance(student: Student) {
        internalDeleteStudentFromAttendance(student.id)
    }

    // used to get all dreams
    @Query("SELECT * FROM attendance d ORDER BY d.date DESC")
    fun getAttendanceDays(): Flow<List<Attendance>>

    // get single dream from id
    @Query("SELECT * FROM attendance WHERE id=(:id)")
    suspend fun internalGetAttendanceDays(id: UUID): Attendance


    // gets dream with entries
    @Transaction
    suspend fun getAttendance(id: UUID): Attendance {
        return internalGetAttendanceDays(id)
    }

    // get single dream from id
    @Query("SELECT * FROM student WHERE schoolId=(:id)")
    suspend fun internalGetStudentBySchoolId(id: String): List<Student>


    // gets dream with entries
    @Transaction
    suspend fun getStudentBySchoolId(id: String): List<Student> {
        return internalGetStudentBySchoolId(id)
    }

    // updates dream
    @Update
    suspend fun internalUpdateAttendance(attendance: Attendance)

    // update dream based on values
    @Transaction
    suspend fun updateAttendance(attendance: Attendance) {
        internalUpdateAttendance(attendance)
    }

    @Query("SELECT * FROM attendanceRecord WHERE studentId =(:studentId) AND attendanceId=(:attendanceId)")
    suspend fun getAttendanceRecord(studentId: UUID, attendanceId: UUID) :AttendanceRecord

    @Insert
    suspend fun internalInsertAttendanceRecord(attendanceRecord: AttendanceRecord)

    @Transaction
    suspend fun insertAttendanceRecord(attendanceRecord: AttendanceRecord) {
        // You must implement this on your own
        internalInsertAttendanceRecord(attendanceRecord)
    }

    @Insert
    suspend fun internalInsertAttendance(attendance: Attendance)

    @Transaction
    suspend fun insertAttendance(attendance: Attendance) {
        // You must implement this on your own
        internalInsertAttendance(attendance)
    }

    @Delete
    suspend fun internalDeleteAttendance(attendance: Attendance)

    @Transaction
    suspend fun deleteAttendance(attendance: Attendance) {
        internalDeleteAttendanceRecordByAttendance(attendanceId = attendance.id)
        internalDeleteAttendance(attendance)
    }

    // used to get all dreams
    @Query("SELECT * FROM student d ORDER BY d.lastName DESC")
    fun getStudents(): Flow<List<Student>>

    // get single dream from id
    @Query("SELECT * FROM student WHERE id=(:id)")
    suspend fun internalGetStudent(id: UUID): Student


    // gets dream with entries
    @Transaction
    suspend fun getStudent(id: UUID): Student {
        return internalGetStudent(id)
    }

    // updates dream
    @Update
    suspend fun internalUpdateStudent(student: Student)

    // update dream based on values
    @Transaction
    suspend fun updateStudent(student: Student) {
        internalUpdateStudent(student)
    }

    @Insert
    suspend fun internalInsertStudent(student: Student)

    @Transaction
    suspend fun insertStudent(student: Student) {
        // You must implement this on your own
        internalInsertStudent(student)
    }

    @Delete
    suspend fun internalDeleteStudent(student: Student)

    @Transaction
    suspend fun deleteStudent(student: Student) {
        internalDeleteStudentFromAttendance(student.id)
        internalDeleteStudent(student)
    }
}