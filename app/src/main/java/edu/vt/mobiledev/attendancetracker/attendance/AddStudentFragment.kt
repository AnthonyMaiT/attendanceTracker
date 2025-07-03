package edu.vt.mobiledev.attendancetracker.attendance

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kotlin.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import edu.vt.mobiledev.attendanceTracker.R
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentAddStudentBinding
import kotlinx.coroutines.launch

class AddStudentFragment: Fragment() {

    // binding with null
    private var _binding: FragmentAddStudentBinding? = null

    // used to get attendance Id
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

        // when user clicks add student
        binding.addStudent.setOnClickListener { view: View ->
            val studentId = binding.studentId.text.toString()
            viewLifecycleOwner.lifecycleScope.launch {
                // get response
                val response = addStudentDetailViewModel.getStudentAndAdd(studentId)
                // response when success
                if (response == AddStudentDetailViewModel.StudentAddStatus.SUCCESS) {
                    Snackbar.make(
                        view,
                        R.string.added_student,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    binding.studentId.setText("")
                // response when student doesn't exist
                } else if (response == AddStudentDetailViewModel.StudentAddStatus.DNE) {
                    Snackbar.make(
                        view,
                        R.string.does_not_exist_student,
                        Snackbar.LENGTH_SHORT
                    ).show()
                // response when multiple students with same ID exists
                } else if (response == AddStudentDetailViewModel.StudentAddStatus.MULT) {
                    Snackbar.make(
                        view,
                        R.string.multiple_add_student,
                        Snackbar.LENGTH_SHORT
                    ).show()
                // response when attendance record already exists
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

    override fun onStop() {
        super.onStop()
        // plays airhorn when fragment is stopped
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.airhorn)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }


    // set binding to null
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}