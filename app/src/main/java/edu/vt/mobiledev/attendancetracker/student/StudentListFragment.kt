package edu.vt.mobiledev.attendancetracker.student

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
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentStudentListBinding
import edu.vt.mobiledev.attendancetracker.Student
import kotlinx.coroutines.launch

class StudentListFragment: Fragment() {
    // binding with null
    private var _binding: FragmentStudentListBinding? = null

    // View Model
    private val studentListViewModel: StudentListViewModel by viewModels()

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
                studentListViewModel.students.collect { students ->
                    if (students.isEmpty()) {
                        binding.noStudentText.visibility = View.VISIBLE
                        binding.noStudentAddButton.visibility = View.VISIBLE
                    }
                    else {
                        binding.noStudentAddButton.visibility = View.GONE
                        binding.noStudentText.visibility = View.GONE
                    }
                    binding.studentRecyclerView.adapter =
                        StudentListAdapter(students) { studentId ->
                            findNavController().navigate(
                                StudentListFragmentDirections.showStudentDetail(studentId)
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
        _binding = FragmentStudentListBinding.inflate(inflater, container, false);

        // for recycler implementation
        binding.studentRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.noStudentAddButton.setOnClickListener {
            showNewStudent()
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            // blank for now
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_student_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.new_student -> {
                        showNewStudent()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        getItemTouchHelper().attachToRecyclerView(binding.studentRecyclerView)

        return binding.root
    }

    private fun showNewStudent() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newStudent = Student()
            studentListViewModel.addStudent(newStudent)
            findNavController().navigate(
                StudentListFragmentDirections.showStudentDetail(newStudent.id)
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
                studentListViewModel.deleteStudent(viewHolder as StudentHolder)
            }
        })
    }

    // set binding to null
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}