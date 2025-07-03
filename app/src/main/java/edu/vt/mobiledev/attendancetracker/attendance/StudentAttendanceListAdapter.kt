package edu.vt.mobiledev.attendancetracker.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.vt.mobiledev.attendanceTracker.databinding.ListItemStudentAttendanceBinding
import edu.vt.mobiledev.attendancetracker.Student
import edu.vt.mobiledev.attendancetracker.setBackgroundWithContrastingText

// student attendance holder class with binding
class StudentAttendanceHolder(
    val binding: ListItemStudentAttendanceBinding
) : RecyclerView.ViewHolder(binding.root) {
    lateinit var boundStudent: Student
        private set

    fun bind(student: Student) {
        boundStudent = student

        // binds student name and set color to grey
        val studentName = student.firstName + " " + student.lastName
        binding.listItemStudent.text = studentName
        binding.listItemStudent.setBackgroundWithContrastingText("grey")

        binding.listItemStudent.isEnabled = false
    }
}

// List adapter
class StudentAttendanceAdapter(
    private val students: List<Student>,
) : RecyclerView.Adapter<StudentAttendanceHolder>() {

    // for view holder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : StudentAttendanceHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemStudentAttendanceBinding.inflate(inflater, parent, false)
        return StudentAttendanceHolder(binding)
    }

    // binding view holder
    override fun onBindViewHolder(holder: StudentAttendanceHolder, position: Int) {
        // binds students to the holder
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount() = students.size
}