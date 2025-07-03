package edu.vt.mobiledev.attendancetracker.database

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
    // used to get students for a certain attendance day
    @Query("""
        SELECT s.*
        FROM student s
        INNER JOIN attendanceRecord ar ON s.id = ar.studentId
        WHERE ar.attendanceId = :attendanceId
    """)
    fun getStudentsForAttendance(attendanceId: UUID): Flow<List<Student>>

    // used to get the counts of students for all attendance days
    @Query("""
        SELECT COUNT(ar.studentId)
        FROM attendance a
        LEFT JOIN attendanceRecord ar ON a.id = ar.attendanceId
        GROUP BY a.id
        ORDER BY a.date DESC
    """)
    suspend fun getStudentsForAttendanceCount(): List<Int>

    // deletes attendanceRecord based of studentId
    @Query("DELETE FROM attendanceRecord WHERE studentId = (:studentId)  ")
    suspend fun internalDeleteStudentFromAttendance(studentId: UUID)

    // deletes attendanceRecord based on attendance Id
    @Query("DELETE FROM attendanceRecord WHERE attendanceId = (:attendanceId)  ")
    suspend fun internalDeleteAttendanceRecordByAttendance(attendanceId: UUID)

    // gets an attendance record based on all fields
    @Query("SELECT * FROM attendanceRecord WHERE studentId =(:studentId) AND attendanceId=(:attendanceId)")
    suspend fun getAttendanceRecord(studentId: UUID, attendanceId: UUID) :AttendanceRecord

    @Query("DELETE FROM attendanceRecord WHERE attendanceId = (:attendanceId) AND studentId  = (:studentId)")
    suspend fun deleteAttendanceRecord(attendanceId: UUID, studentId: UUID)

    // inserting attendance record
    @Insert
    suspend fun internalInsertAttendanceRecord(attendanceRecord: AttendanceRecord)

    // inserting attendance record
    @Transaction
    suspend fun insertAttendanceRecord(attendanceRecord: AttendanceRecord) {
        // You must implement this on your own
        internalInsertAttendanceRecord(attendanceRecord)
    }

    // used to get all attendance days
    @Query("SELECT * FROM attendance d ORDER BY d.date DESC")
    fun getAttendanceDays(): Flow<List<Attendance>>

    // get single attendance from id
    @Query("SELECT * FROM attendance WHERE id=(:id)")
    suspend fun internalGetAttendanceDays(id: UUID): Attendance


    // gets single attendance day
    @Transaction
    suspend fun getAttendance(id: UUID): Attendance {
        return internalGetAttendanceDays(id)
    }

    // updates attendance day
    @Update
    suspend fun internalUpdateAttendance(attendance: Attendance)

    // update attendance based on values
    @Transaction
    suspend fun updateAttendance(attendance: Attendance) {
        internalUpdateAttendance(attendance)
    }


    // inserting attendance
    @Insert
    suspend fun internalInsertAttendance(attendance: Attendance)

    // inserts attendance
    @Transaction
    suspend fun insertAttendance(attendance: Attendance) {
        // You must implement this on your own
        internalInsertAttendance(attendance)
    }

    // deletes attendance
    @Delete
    suspend fun internalDeleteAttendance(attendance: Attendance)

    // deletes attendance and all records with attendance
    @Transaction
    suspend fun deleteAttendance(attendance: Attendance) {
        internalDeleteAttendanceRecordByAttendance(attendanceId = attendance.id)
        internalDeleteAttendance(attendance)
    }

    // get single student from id
    @Query("SELECT * FROM student WHERE schoolId=(:id)")
    suspend fun internalGetStudentBySchoolId(id: String): List<Student>


    // get student from id
    @Transaction
    suspend fun getStudentBySchoolId(id: String): List<Student> {
        return internalGetStudentBySchoolId(id)
    }

    // used to get all students
    @Query("SELECT * FROM student d ORDER BY d.lastName DESC")
    fun getStudents(): Flow<List<Student>>

    // get single student from id
    @Query("SELECT * FROM student WHERE id=(:id)")
    suspend fun internalGetStudent(id: UUID): Student


    // gets student
    @Transaction
    suspend fun getStudent(id: UUID): Student {
        return internalGetStudent(id)
    }

    // updates student
    @Update
    suspend fun internalUpdateStudent(student: Student)

    // update student based on values
    @Transaction
    suspend fun updateStudent(student: Student) {
        internalUpdateStudent(student)
    }

    // inserts student
    @Insert
    suspend fun internalInsertStudent(student: Student)

    // inserts student
    @Transaction
    suspend fun insertStudent(student: Student) {
        // You must implement this on your own
        internalInsertStudent(student)
    }

    // deletes student
    @Delete
    suspend fun internalDeleteStudent(student: Student)

    // deletes student and all attendance records from it
    @Transaction
    suspend fun deleteStudent(student: Student) {
        internalDeleteStudentFromAttendance(student.id)
        internalDeleteStudent(student)
    }
}