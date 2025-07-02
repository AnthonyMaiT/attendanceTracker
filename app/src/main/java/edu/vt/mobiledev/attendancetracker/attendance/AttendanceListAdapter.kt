package edu.vt.mobiledev.attendancetracker.attendance

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.vt.mobiledev.attendanceTracker.databinding.ListItemAttendanceBinding
import edu.vt.mobiledev.attendancetracker.Attendance
import java.util.UUID

// Dream holder class with binding
class AttendanceHolder(
    val binding: ListItemAttendanceBinding
) : RecyclerView.ViewHolder(binding.root) {
    lateinit var boundAttendance: Attendance
        private set

    fun bind(attendance: Attendance, studentCount: Int, onAttendanceClick: (attendanceId: UUID) -> Unit){
        boundAttendance = attendance

        val formatString = "MM-dd-yyyy"
        val date = DateFormat.format(formatString, attendance.date).toString()
        binding.listItemDate.text = date

        val studentCountText = "Students Present: " + studentCount.toString()
        binding.listItemStudentPresent.text = studentCountText

        binding.root.setOnClickListener {
            onAttendanceClick(attendance.id)
        }
    }

}

// List adapter
class AttendanceListAdapter(
    private val attendance: List<Attendance>,
    private val studentCounts: List<Int>,
    private val onAttendanceClick: (attendanceId: UUID) -> Unit
) : RecyclerView.Adapter<AttendanceHolder>() {

    // for view holder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : AttendanceHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemAttendanceBinding.inflate(inflater, parent, false)
        return AttendanceHolder(binding)
    }

    // binding view holder
    override fun onBindViewHolder(holder: AttendanceHolder, position: Int) {
        // binds dream to the holder
        val attendanceDay = attendance[position]
        val studentCount = studentCounts[position]
        holder.bind(attendanceDay, studentCount, onAttendanceClick)
    }

    override fun getItemCount() = attendance.size
}