package edu.vt.mobiledev.attendancetracker.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.vt.mobiledev.attendanceTracker.databinding.ListItemStudentAttendanceBinding
import edu.vt.mobiledev.attendancetracker.Student
import edu.vt.mobiledev.attendancetracker.setBackgroundWithContrastingText

// Dream holder class with binding
class StudentAttendanceHolder(
    val binding: ListItemStudentAttendanceBinding
) : RecyclerView.ViewHolder(binding.root) {
    lateinit var boundStudent: Student
        private set

    fun bind(student: Student) {
        boundStudent = student

        val studentName = student.firstName + " " + student.lastName
        binding.listItemStudent.text = studentName
        binding.listItemStudent.setBackgroundWithContrastingText("grey")

        binding.listItemStudent.isEnabled = false
    }
}

// List adapter
class StudentAttendanceAdapter(
    private val entries: List<Student>,
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
        // binds entry to the holder
        val entry = entries[position]
        holder.bind(entry)
    }

    override fun getItemCount() = entries.size
}