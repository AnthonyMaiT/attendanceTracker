package edu.vt.mobiledev.attendancetracker.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.vt.mobiledev.attendancetracker.Student
import edu.vt.mobiledev.dreamcatcher.database.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class StudentDetailViewModel(studentId: UUID) : ViewModel() {
    // dream repo object
    private val attendanceRepository = AttendanceRepository.get()

    // set dream
    private val _student: MutableStateFlow<Student?> = MutableStateFlow(null)
    val student: StateFlow<Student?> = _student.asStateFlow()
    init {
        viewModelScope.launch {
            _student.value = attendanceRepository.getStudent(studentId)
        }
    }

    // updates dream into repo
    fun updateStudent(onUpdate: (Student) -> Student) {
        _student.update { oldStudent ->
            val newStudent = oldStudent?.let { onUpdate(it) } ?: return
            if (newStudent == oldStudent) {
                return
            }
            // update last updated text
            newStudent.copy()
        }
    }

    // push changes to repo when view is closed
    override fun onCleared() {
        super.onCleared()

        student.value?.let { attendanceRepository.updateStudent(it) }
    }
}

// used for arguments for dreamID
class StudentDetailViewModelFactory(
    private val studentId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudentDetailViewModel(studentId) as T
    }
}
