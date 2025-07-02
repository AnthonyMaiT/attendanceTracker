package edu.vt.mobiledev.attendancetracker.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.vt.mobiledev.attendancetracker.Attendance
import edu.vt.mobiledev.attendancetracker.Student
import edu.vt.mobiledev.dreamcatcher.database.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AttendanceDetailViewModel(attendanceId: UUID) : ViewModel() {
    // dream repo object
    private val attendanceRepository = AttendanceRepository.get()

    // set dream
    private val _attendance: MutableStateFlow<Attendance?> = MutableStateFlow(null)
    val attendance: StateFlow<Attendance?> = _attendance.asStateFlow()

    private val _studentsForAttendance: MutableStateFlow<List<Student>> = MutableStateFlow(emptyList())
    // get all dreams
    val studentsForAttendance: StateFlow<List<Student>>
        get() = _studentsForAttendance.asStateFlow()

    fun deleteStudentFromAttendance(student: StudentAttendanceHolder) {
        attendanceRepository.deleteStudentFromAttendance(student.boundStudent)
    }

    init {
        viewModelScope.launch {
            _attendance.value = attendanceRepository.getAttendance(attendanceId)
            attendanceRepository.getStudentsForAttendance(attendanceId).collect {
                _studentsForAttendance.value = it
            }
        }
    }

    // updates dream into repo
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

        attendance.value?.let { attendanceRepository.updateAttendance(it) }
    }
}

// used for arguments for dreamID
class AttendanceDetailViewModelFactory(
    private val attendanceId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AttendanceDetailViewModel(attendanceId) as T
    }
}
