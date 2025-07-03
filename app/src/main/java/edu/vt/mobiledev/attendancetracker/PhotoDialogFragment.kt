package edu.vt.mobiledev.attendancetracker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.core.view.doOnLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import edu.vt.mobiledev.attendanceTracker.databinding.FragmentPhotoDialogBinding
import java.io.File

class PhotoDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentPhotoDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = FragmentPhotoDialogBinding.inflate(layoutInflater)

        val args: PhotoDialogFragmentArgs by navArgs()
        val photoFileName = args.dreamPhotoFileName

        // sets photo
        updatePhoto(photoFileName)


        // builds dialog box with functions for photo
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .show()

    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.photoDetail.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }

            if (photoFile?.exists() == true) {
                binding.root.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.photoDetail.setImageBitmap(scaledBitmap)
                    binding.photoDetail.tag = photoFileName
                }
            } else {
                binding.photoDetail.setImageBitmap(null)
                binding.photoDetail.tag = null
            }
        }

    }
}