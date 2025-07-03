package edu.vt.mobiledev.attendancetracker.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.vt.mobiledev.attendancetracker.Student
import edu.vt.mobiledev.attendancetracker.database.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentListViewModel : ViewModel() {

    // call attendance repo
    private val attendanceRepository = AttendanceRepository.get()

    private val _students: MutableStateFlow<List<Student>> = MutableStateFlow(emptyList())
    // get all students
    val students: StateFlow<List<Student>>
        get() = _students.asStateFlow()

    // add student
    suspend fun addStudent(student: Student) {
        attendanceRepository.addStudent(student)
    }

    //delete student
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
