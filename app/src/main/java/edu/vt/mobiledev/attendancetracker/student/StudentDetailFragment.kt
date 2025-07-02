package edu.vt.mobiledev.attendancetracker.student

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
import edu.vt.mobiledev.attendanceTracker.R
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentStudentDetailBinding
import edu.vt.mobiledev.attendancetracker.DatePickerFragment
import edu.vt.mobiledev.attendancetracker.Student
import edu.vt.mobiledev.attendancetracker.getScaledBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class StudentDetailFragment: Fragment() {

    // binding with null
    private var _binding: FragmentStudentDetailBinding? = null

    private val args: StudentDetailFragmentArgs by navArgs()

    // View Model
    private val studentDetailViewModel: StudentDetailViewModel by viewModels {
        StudentDetailViewModelFactory(args.studentId)
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

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        // Handle the result
        if (didTakePhoto) {
            binding.studentPhoto.tag = null
            studentDetailViewModel.student.value?.let {
                updatePhoto(it)
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { requestKey, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            studentDetailViewModel.updateStudent { it.copy(birthDate = newDate) }
        }
    }

    // On Create View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // for binding
        _binding = FragmentStudentDetailBinding.inflate(inflater, container, false);

        requireActivity().addMenuProvider(object : MenuProvider {
            // blank for now
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_student_detail, menu)
                val captureImageIntent = takePhoto.contract.createIntent(
                    requireContext(),
                    Uri.EMPTY // NOTE: The "null" used in BNRG is obsolete now
                )
                menu.findItem(R.id.take_photo_menu).isVisible = canResolveIntent(captureImageIntent)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.take_photo_menu -> {
                        studentDetailViewModel.student.value?.let {
                            val photoFile = File(
                                requireContext().applicationContext.filesDir,
                                it.photoFileName
                            )
                            val photoUri = FileProvider.getUriForFile(
                                requireContext(),
                                "edu.vt.mobiledev.attendancetracker.fileprovider",
                                photoFile
                            )
                            takePhoto.launch(photoUri)
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)

        // set title based on text box change
        binding.firstNameText.doOnTextChanged { text, _, _, _ ->
            studentDetailViewModel.updateStudent { oldStudent ->
                oldStudent.copy(firstName = text.toString())
            }
        }
        binding.lastNameText.doOnTextChanged { text, _, _, _ ->
            studentDetailViewModel.updateStudent { oldStudent ->
                oldStudent.copy(lastName = text.toString())
            }
        }
        binding.schoolIdText.doOnTextChanged { text, _, _, _ ->
            studentDetailViewModel.updateStudent { oldStudent ->
                oldStudent.copy(schoolId = text.toString())
            }
        }

        // update the UI
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                studentDetailViewModel.student.collect { student ->
                    student?.let { updateUi(it) }
                }
            }
        }

        return binding.root
    }

    private fun updateUi(student: Student) {

        binding.apply {

            studentPhoto.setOnClickListener {
                findNavController().navigate(
                    StudentDetailFragmentDirections.showPhotoDetail(student.photoFileName)
                )
            }

            studentBirthDate.setOnClickListener {
                findNavController().navigate(
                    StudentDetailFragmentDirections.selectDate(student.birthDate)
                )
            }

            // update title text
            if (firstNameText.text.toString() != student.firstName) {
                firstNameText.setText(student.firstName)
            }
            if (lastNameText.text.toString() != student.lastName) {
                lastNameText.setText(student.lastName)
            }
            if (schoolIdText.text.toString() != student.schoolId) {
                schoolIdText.setText(student.schoolId)
            }
            // Set date based on object
            val formatString = "MM-dd-yyyy"
            val birthDay = DateFormat.format(formatString, student.birthDate).toString()
            val birthDayText = "Birthday: " + birthDay
            if (studentBirthDate.text.toString() != birthDayText) {
                binding.studentBirthDate.text = birthDayText
            }
        }
        updatePhoto(student)
    }

    private fun updatePhoto(student: Student) {
        with(binding.studentPhoto) {
            if (tag != student.photoFileName) {
                val photoFile =
                    File(requireContext().applicationContext.filesDir, student.photoFileName)
                if (photoFile.exists()) {
                    doOnLayout { measuredView ->
                        val scaledBM = getScaledBitmap(
                            photoFile.path,
                            measuredView.width,
                            measuredView.height
                        )
                        setImageBitmap(scaledBM)
                        tag = student.photoFileName
                        isEnabled = true
                    }
                } else {
                    setImageBitmap(null)
                    tag = null
                    binding.studentPhoto.isEnabled = false
                }
            }
        }
    }

    // set binding to null
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}