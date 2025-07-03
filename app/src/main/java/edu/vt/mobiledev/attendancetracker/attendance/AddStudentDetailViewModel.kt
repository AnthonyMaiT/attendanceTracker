package edu.vt.mobiledev.attendancetracker.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.vt.mobiledev.attendancetracker.AttendanceRecord
import edu.vt.mobiledev.attendancetracker.database.AttendanceRepository
import java.util.UUID

class AddStudentDetailViewModel(private val attendanceId: UUID) : ViewModel() {
    // attendance repo
    private val attendanceRepository = AttendanceRepository.get()

    // adds a new attendance record
    private suspend fun addStudentToAttendance(attendanceRecord: AttendanceRecord) {
        attendanceRepository.addAttendanceRecord(attendanceRecord)
    }

    // Used to add new attendance record using schoolId
    suspend fun getStudentAndAdd(schoolId: String) : StudentAddStatus {
        // gets student by ID
        val s = attendanceRepository.getStudentBySchoolId(schoolId)
        if (s.isEmpty()) {
            // return DNE when no student found
            return StudentAddStatus.DNE
        }
        if (s.size > 1) {
            // return error when multiple students with same ID
            return StudentAddStatus.MULT
        }
        // Used to get duplicate records
        val ar = AttendanceRecord(attendanceId = attendanceId, studentId = s[0].id)
        val getAr = attendanceRepository.getAttendanceRecord(ar)
        if (getAr != null) {
            return StudentAddStatus.DUP
        }
        // add attendance record
        addStudentToAttendance(ar)
        return StudentAddStatus.SUCCESS
    }

    // used to status returns when adding student
    enum class StudentAddStatus {
        DNE,
        MULT,
        DUP,
        SUCCESS
    }
}

// used for arguments for attendanceId
class AddStudentDetailViewModelFactory(
    private val attendanceId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddStudentDetailViewModel(attendanceId) as T
    }
}
