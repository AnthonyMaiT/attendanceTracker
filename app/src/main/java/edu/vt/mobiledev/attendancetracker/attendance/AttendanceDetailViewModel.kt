package edu.vt.mobiledev.attendancetracker.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.vt.mobiledev.attendancetracker.Attendance
import edu.vt.mobiledev.attendancetracker.Student
import edu.vt.mobiledev.attendancetracker.database.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AttendanceDetailViewModel(private val attendanceId: UUID) : ViewModel() {
    // attendance repo object
    private val attendanceRepository = AttendanceRepository.get()

    // set attendance
    private val _attendance: MutableStateFlow<Attendance?> = MutableStateFlow(null)
    val attendance: StateFlow<Attendance?> = _attendance.asStateFlow()

    private val _studentsForAttendance: MutableStateFlow<List<Student>> = MutableStateFlow(emptyList())
    // get all students inside attendance
    val studentsForAttendance: StateFlow<List<Student>>
        get() = _studentsForAttendance.asStateFlow()

    // delete student from attendance
    fun deleteStudentFromAttendance(student: StudentAttendanceHolder) {
        attendanceRepository.deleteAttendanceRecord(attendanceId, student.boundStudent.id)
    }

    init {
        viewModelScope.launch {
            // initiate values for flow lists
            _attendance.value = attendanceRepository.getAttendance(attendanceId)
            attendanceRepository.getStudentsForAttendance(attendanceId).collect {
                _studentsForAttendance.value = it
            }
        }
    }

    // updates attendance into repo
    fun updateAttendance(onUpdate: (Attendance) -> Attendance) {
        _attendance.update { oldAttendance ->
            val newAttendance = oldAttendance?.let { onUpdate(it) } ?: return
            if (newAttendance == oldAttendance) {
                return
            }
            // update last updated text
            newAttendance.copy()
        }
    }

    // push changes to repo when view is closed
    override fun onCleared() {
        super.onCleared()
        // update attendance
        attendance.value?.let { attendanceRepository.updateAttendance(it) }
    }
}

// used for arguments for attendanceId
class AttendanceDetailViewModelFactory(
    private val attendanceId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AttendanceDetailViewModel(attendanceId) as T
    }
}
