package edu.vt.mobiledev.attendancetracker.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.vt.mobiledev.attendancetracker.Attendance
import edu.vt.mobiledev.attendancetracker.database.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AttendanceListViewModel : ViewModel() {

    // call attendance repo
    private val attendanceRepository = AttendanceRepository.get()

    private val _attendance: MutableStateFlow<List<Attendance>> = MutableStateFlow(emptyList())
    // get all attendance
    val attendance: StateFlow<List<Attendance>>
        get() = _attendance.asStateFlow()
    // add attendance function
    suspend fun addAttendance(attendance: Attendance) {
        attendanceRepository.addAttendance(attendance)
    }
    // get student count for every attendance
    suspend fun getStudentInAttendanceCount(): List<Int> {
        return attendanceRepository.getStudentsForAttendanceCount()
    }
    // delete attendance
     fun deleteAttendance(attendance: AttendanceHolder) {
        attendanceRepository.deleteAttendance(attendance.boundAttendance)
    }

    init {
        viewModelScope.launch {
            // initiate attendance value
            attendanceRepository.getAttendanceDays().collect {
                _attendance.value = it
            }
        }
    }
}
