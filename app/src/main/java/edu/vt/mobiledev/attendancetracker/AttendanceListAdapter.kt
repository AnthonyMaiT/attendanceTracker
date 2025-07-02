package edu.vt.mobiledev.attendancetracker

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import edu.vt.mobiledev.attendanceTracker.databinding.ListItemAttendanceBinding
import edu.vt.mobiledev.attendanceTracker.databinding.ListItemStudentBinding
import java.io.File
import java.util.UUID

// Dream holder class with binding
class AttendanceHolder(
    val binding: ListItemAttendanceBinding
) : RecyclerView.ViewHolder(binding.root) {
    lateinit var boundAttendance: Attendance
        private set

    fun bind(attendance: Attendance , onAttendanceClick: (attendanceId: UUID) -> Unit){
        boundAttendance = attendance

        val formatString = "MM-dd-yyyy"
        val date = DateFormat.format(formatString, attendance.date).toString()
        binding.listItemDate.text = date

        binding.root.setOnClickListener {
            onAttendanceClick(attendance.id)
        }
    }

}

// List adapter
class AttendanceListAdapter(
    private val attendance: List<Attendance>,
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
        holder.bind(attendanceDay, onAttendanceClick)
    }

    override fun getItemCount() = attendance.size
}