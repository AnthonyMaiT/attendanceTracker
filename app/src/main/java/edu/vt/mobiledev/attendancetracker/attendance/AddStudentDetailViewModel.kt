package edu.vt.mobiledev.attendancetracker.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.vt.mobiledev.attendancetracker.AttendanceRecord
import edu.vt.mobiledev.dreamcatcher.database.AttendanceRepository
import java.util.UUID

class AddStudentDetailViewModel(private val attendanceId: UUID) : ViewModel() {
    // dream repo object
    private val attendanceRepository = AttendanceRepository.get()

    // updates dream into repo
    suspend fun addStudentToAttendance(attendanceRecord: AttendanceRecord) {
        attendanceRepository.addAttendanceRecord(attendanceRecord)
    }

    suspend fun getStudentAndAdd(schoolId: String) : StudentAddStatus {
        val s = attendanceRepository.getStudentBySchoolId(schoolId)
        if (s.isEmpty()) {
            return StudentAddStatus.DNE
        }
        if (s.size > 1) {
            return StudentAddStatus.MULT
        }
        val ar = AttendanceRecord(attendanceId = attendanceId, studentId = s[0].id)
        val getAr = attendanceRepository.getAttendanceRecord(ar)
        if (getAr != null) {
            return StudentAddStatus.DUP
        }
        addStudentToAttendance(ar)
        return StudentAddStatus.SUCCESS
    }

    enum class StudentAddStatus {
        DNE,
        MULT,
        DUP,
        SUCCESS
    }
    // push changes to repo when view is closed
    override fun onCleared() {
        super.onCleared()
    }
}

// used for arguments for dreamID
class AddStudentDetailViewModelFactory(
    private val attendanceId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddStudentDetailViewModel(attendanceId) as T
    }
}
