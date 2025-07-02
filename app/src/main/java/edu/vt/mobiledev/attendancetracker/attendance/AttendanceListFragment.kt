package edu.vt.mobiledev.attendancetracker.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.vt.mobiledev.attendanceTracker.R
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentAttendanceListBinding
import edu.vt.mobiledev.attendancetracker.Attendance
import kotlinx.coroutines.launch

class AttendanceListFragment: Fragment() {
    // binding with null
    private var _binding: FragmentAttendanceListBinding? = null

    // View Model
    private val attendanceListViewModel: AttendanceListViewModel by viewModels()

    // binding
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                attendanceListViewModel.attendance.collect { attendance ->
                    if (attendance.isEmpty()) {
                        binding.noAttendanceText.visibility = View.VISIBLE
                        binding.noAttendanceAddButton.visibility = View.VISIBLE
                    }
                    else {
                        binding.noAttendanceText.visibility = View.GONE
                        binding.noAttendanceAddButton.visibility = View.GONE
                    }
                    binding.attendanceRecyclerView.adapter =
                        AttendanceListAdapter(attendance) { attendanceId ->
                            findNavController().navigate(
                                AttendanceListFragmentDirections.showAttendanceDetail(attendanceId)
                            )
                        }
                }
            }
        }
    }

    // on create view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // for binding
        _binding = FragmentAttendanceListBinding.inflate(inflater, container, false);

        // for recycler implementation
        binding.attendanceRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.noAttendanceAddButton.setOnClickListener {
            showNewAttendance()
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            // blank for now
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_attendance_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.new_attendance -> {
                        showNewAttendance()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        getItemTouchHelper().attachToRecyclerView(binding.attendanceRecyclerView)

        return binding.root
    }

    private fun showNewAttendance() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newAttendance = Attendance()
            attendanceListViewModel.addAttendance(newAttendance)
            findNavController().navigate(
                AttendanceListFragmentDirections.showAttendanceDetail(newAttendance.id)
            )
        }
    }

    private fun getItemTouchHelper(): ItemTouchHelper {

        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                attendanceListViewModel.deleteAttendance(viewHolder as AttendanceHolder)
            }
        })
    }

    // set binding to null
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}