package edu.vt.mobiledev.attendancetracker.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import edu.vt.mobiledev.attendanceTracker.databinding.ListItemStudentBinding
import edu.vt.mobiledev.attendancetracker.Student
import edu.vt.mobiledev.attendancetracker.getScaledBitmap
import java.io.File
import java.util.UUID

// Dream holder class with binding
class StudentHolder(
    val binding: ListItemStudentBinding
) : RecyclerView.ViewHolder(binding.root) {
    lateinit var boundStudent: Student
        private set

    fun bind(student: Student, onStudentClicked: (studentId: UUID) -> Unit){
        boundStudent = student

        // set title
        val studentName = student.firstName + " " + student.lastName
        binding.listItemName.text = studentName
        // set reflection count
        val studId = "Student ID: " + student.schoolId
        binding.listItemSchoolId.text = studId

        updatePhoto(student)

        binding.root.setOnClickListener {
            onStudentClicked(student.id)
        }
    }

    private fun updatePhoto(student: Student) {
        with(binding.listItemImage) {
            if (tag != student.photoFileName) {
                val photoFile =
                    File(binding.root.context.filesDir, student.photoFileName)
                if (photoFile.exists()) {
                    doOnLayout { measuredView ->
                        val scaledBM = getScaledBitmap(
                            photoFile.path,
                            measuredView.width,
                            measuredView.height
                        )
                        setImageBitmap(scaledBM)
                        tag = student.photoFileName
                        binding.listItemImage.visibility = View.VISIBLE
                    }
                } else {
                    setImageBitmap(null)
                    tag = null
                    binding.listItemImage.visibility = View.GONE
                }
            }
        }
    }
}

// List adapter
class StudentListAdapter(
    private val students: List<Student>,
    private val onStudentClicked: (studentId: UUID) -> Unit
) : RecyclerView.Adapter<StudentHolder>() {

    // for view holder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : StudentHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemStudentBinding.inflate(inflater, parent, false)
        return StudentHolder(binding)
    }

    // binding view holder
    override fun onBindViewHolder(holder: StudentHolder, position: Int) {
        // binds dream to the holder
        val student = students[position]
        holder.bind(student, onStudentClicked)
    }

    override fun getItemCount() = students.size
}