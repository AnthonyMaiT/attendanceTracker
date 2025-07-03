package edu.vt.mobiledev.attendancetracker.attendance

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kotlin.getValue
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.vt.mobiledev.attendanceTracker.R
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentAttendanceDetailBinding
import edu.vt.mobiledev.attendancetracker.Attendance
import edu.vt.mobiledev.attendancetracker.DatePickerFragment
import kotlinx.coroutines.launch
import java.util.Date

class AttendanceDetailFragment: Fragment() {

    // binding with null
    private var _binding: FragmentAttendanceDetailBinding? = null

    // args for attendanceId
    private val args: AttendanceDetailFragmentArgs by navArgs()

    // View Model
    private val attendanceDetailViewModel: AttendanceDetailViewModel by viewModels {
        AttendanceDetailViewModelFactory(args.attendanceId)
    }

    // binding
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    // On create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // on view created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // recycler for student list
                attendanceDetailViewModel.studentsForAttendance.collect { studentsForAttendance ->
                    studentsForAttendance?.let { binding.attendanceListRecycler.adapter = StudentAttendanceAdapter(it) }
                }
            }
        }

        // for date picker result
        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { requestKey, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            // update date
            attendanceDetailViewModel.updateAttendance { it.copy(date = newDate) }
        }
    }

    // On Create View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // for binding
        _binding = FragmentAttendanceDetailBinding.inflate(inflater, container, false);

        // for recycler view
        binding.attendanceListRecycler.layoutManager = LinearLayoutManager(context);

        // for menus
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_attendance_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    // initiate share for attendance
                    R.id.share_attendance -> {
                        attendanceDetailViewModel.attendance.value?.let { shareAttendance(it) }
                        return true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)

        // update the UI
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                attendanceDetailViewModel.attendance.collect { attendance ->
                    attendance?.let { updateUi(it) }
                }
            }
        }

        // swipe feature for deleting student records
        getItemTouchHelper().attachToRecyclerView(binding.attendanceListRecycler)

        return binding.root
    }

    private fun updateUi(attendance: Attendance) {

        binding.apply {

            // add click listener for showing date dialog
            date.setOnClickListener {
                findNavController().navigate(
                    AttendanceDetailFragmentDirections.selectDate(attendance.date)
                )
            }

            // click listener for adding students
            addStudents.setOnClickListener {
                findNavController().navigate(
                    AttendanceDetailFragmentDirections.addStudents(attendance.id)
                )
            }

            // Set date based on object
            val formatString = "MM-dd-yyyy"
            val attendanceDay = DateFormat.format(formatString, attendance.date).toString()
            val attendanceDayText = "Date: " + attendanceDay
            if (date.text.toString() != attendanceDayText) {
                binding.date.text = attendanceDayText
            }
        }
    }

    // used to get report of attendance
    private fun getAttendanceReport(attendance: Attendance): String {

        var attendanceReport: List<String>
        // format date and add to list
        val formatString = "MM-dd-yyyy"
        val attendanceDate = DateFormat.format(formatString, attendance.date).toString()

        attendanceReport = listOf(attendanceDate)

        // add each student to list
        val sl = attendanceDetailViewModel.studentsForAttendance
        viewLifecycleOwner.lifecycleScope.launch {
            sl.collect { studentList ->
                studentList.forEach {
                    val name = it.firstName + " " + it.lastName
                    attendanceReport += listOf(name)
                }
            }
        }

        // split list with new line characters
        return attendanceReport.joinToString("\n")
    }

    // used to share attendance with intent
    private fun shareAttendance(attendance: Attendance) {
        val reportIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getAttendanceReport(attendance))
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.report_subject)
            )
        }

        // wrap report intent in chooser intent to choose apps
        val chooserIntent = Intent.createChooser(
            reportIntent,
            getString(R.string.send_report)
        )

        startActivity(chooserIntent)
    }

    // for swipe feature
    private fun getItemTouchHelper(): ItemTouchHelper {

        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            // delete when item is swiped
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                attendanceDetailViewModel.deleteStudentFromAttendance(viewHolder as StudentAttendanceHolder)
            }
        })
    }



    // set binding to null
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}