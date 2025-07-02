package edu.vt.mobiledev.attendancetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.vt.mobiledev.dreamcatcher.database.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentListViewModel : ViewModel() {

    // call dream repo
    private val attendanceRepository = AttendanceRepository.get()

    private val _students: MutableStateFlow<List<Student>> = MutableStateFlow(emptyList())
    // get all dreams
    val students: StateFlow<List<Student>>
        get() = _students.asStateFlow()

    suspend fun addStudent(student: Student) {
        attendanceRepository.addStudent(student)
    }

     fun deleteStudent(student: StudentHolder) {
        attendanceRepository.deleteStudent(student.boundStudent)
    }

    init {
        viewModelScope.launch {
            attendanceRepository.getStudents().collect {
                _students.value = it
            }
        }
    }
}
