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
import com.google.android.material.snackbar.Snackbar
import edu.vt.mobiledev.attendanceTracker.R
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentAddStudentBinding
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentStudentDetailBinding
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class AddStudentFragment: Fragment() {

    // binding with null
    private var _binding: FragmentAddStudentBinding? = null

    private val args: AddStudentFragmentArgs by navArgs()

    // View Model
    private val addStudentDetailViewModel: AddStudentDetailViewModel by viewModels {
        AddStudentDetailViewModelFactory(args.attendanceId)
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

    // On Create View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // for binding
        _binding = FragmentAddStudentBinding.inflate(inflater, container, false);

        binding.addStudent.setOnClickListener { view: View ->
            val studentId = binding.studentId.text.toString()
            viewLifecycleOwner.lifecycleScope.launch {
                val response = addStudentDetailViewModel.getStudentAndAdd(studentId)
                if (response == AddStudentDetailViewModel.StudentAddStatus.SUCCESS) {
                    Snackbar.make(
                        view,
                        R.string.added_student,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    binding.studentId.setText("")
                } else if (response == AddStudentDetailViewModel.StudentAddStatus.DNE) {
                    Snackbar.make(
                        view,
                        R.string.does_not_exist_student,
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else if (response == AddStudentDetailViewModel.StudentAddStatus.MULT) {
                    Snackbar.make(
                        view,
                        R.string.multiple_add_student,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }  else if (response == AddStudentDetailViewModel.StudentAddStatus.DUP) {
                    Snackbar.make(
                        view,
                        R.string.duplicate_add_student,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

            }
        }

        return binding.root
    }


    // set binding to null
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}