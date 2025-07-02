package edu.vt.mobiledev.attendancetracker

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kotlin.getValue
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
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
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentStudentDetailBinding
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class AttendanceDetailFragment: Fragment() {

    // binding with null
    private var _binding: FragmentAttendanceDetailBinding? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                attendanceDetailViewModel.studentsForAttendance.collect { studentsForAttendance ->
                    studentsForAttendance?.let { binding.attendanceListRecycler.adapter = StudentAttendanceAdapter(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { requestKey, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
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

        binding.attendanceListRecycler.layoutManager = LinearLayoutManager(context);

        requireActivity().addMenuProvider(object : MenuProvider {
            // blank for now
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_attendance_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
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

        getItemTouchHelper().attachToRecyclerView(binding.attendanceListRecycler)

        return binding.root
    }

    private fun updateUi(attendance: Attendance) {

        binding.apply {

            date.setOnClickListener {
                findNavController().navigate(
                    AttendanceDetailFragmentDirections.selectDate(attendance.date)
                )
            }

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

    private fun getAttendanceReport(attendance: Attendance): String {

        var attendanceReport: List<String>

        val formatString = "MM-dd-yyyy"
        val attendanceDate = DateFormat.format(formatString, attendance.date).toString()

        attendanceReport = listOf(attendanceDate)

        val sl = attendanceDetailViewModel.studentsForAttendance
        viewLifecycleOwner.lifecycleScope.launch {
            sl.collect { studentList ->
                studentList.forEach {
                    val name = it.firstName + " " + it.lastName
                    attendanceReport += listOf(name)
                }
            }
        }


        return attendanceReport.joinToString("\n")
    }

    private fun shareAttendance(attendance: Attendance) {
        val reportIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getAttendanceReport(attendance))
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.report_subject)
            )
        }

        val chooserIntent = Intent.createChooser(
            reportIntent,
            getString(R.string.send_report)
        )

        startActivity(chooserIntent)
    }

    private fun getItemTouchHelper(): ItemTouchHelper {

        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

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