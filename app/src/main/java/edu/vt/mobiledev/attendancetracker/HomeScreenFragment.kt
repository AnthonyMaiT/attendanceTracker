package edu.vt.mobiledev.attendancetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentHomeScreenBinding

class HomeScreenFragment: Fragment() {
    // binding with null
    private var _binding: FragmentHomeScreenBinding? = null

    // binding
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // on create view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // for binding
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false);

        // views student list
        binding.viewStudent.setOnClickListener {
            findNavController().navigate(
                HomeScreenFragmentDirections.viewStudents()
            )
        }

        // views attendance list
        binding.viewAttendance.setOnClickListener {
            findNavController().navigate(
                HomeScreenFragmentDirections.viewAttendance()
            )
        }


        return binding.root
    }

    // set binding to null
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}