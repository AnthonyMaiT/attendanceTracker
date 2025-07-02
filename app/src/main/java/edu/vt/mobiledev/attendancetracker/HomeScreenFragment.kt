package edu.vt.mobiledev.attendancetracker

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
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentHomeScreenBinding
import kotlinx.coroutines.launch

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

        binding.viewStudent.setOnClickListener {
            findNavController().navigate(
                HomeScreenFragmentDirections.viewStudents()
            )
        }

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