package edu.vt.mobiledev.attendancetracker.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.vt.mobiledev.attendancetracker.Student
import edu.vt.mobiledev.attendancetracker.database.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class StudentDetailViewModel(studentId: UUID) : ViewModel() {
    // attendance repo object
    private val attendanceRepository = AttendanceRepository.get()

    // set student
    private val _student: MutableStateFlow<Student?> = MutableStateFlow(null)
    val student: StateFlow<Student?> = _student.asStateFlow()
    init {
        viewModelScope.launch {
            _student.value = attendanceRepository.getStudent(studentId)
        }
    }

    // updates student into repo
    fun updateStudent(onUpdate: (Student) -> Student) {
        _student.update { oldStudent ->
            val newStudent = oldStudent?.let { onUpdate(it) } ?: return
            if (newStudent == oldStudent) {
                return
            }
            newStudent.copy()
        }
    }

    // push changes to repo when view is closed
    override fun onCleared() {
        super.onCleared()

        student.value?.let { attendanceRepository.updateStudent(it) }
    }
}

// used for arguments for studentId
class StudentDetailViewModelFactory(
    private val studentId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudentDetailViewModel(studentId) as T
    }
}
