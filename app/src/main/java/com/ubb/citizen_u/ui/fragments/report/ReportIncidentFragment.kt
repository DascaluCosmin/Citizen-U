package com.ubb.citizen_u.ui.fragments.report

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.ubb.citizen_u.databinding.FragmentReportIncidentBinding
import com.ubb.citizen_u.ui.fragments.toastErrorMessage
import java.io.File

class ReportIncidentFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ReportIncidentFragment"
        private const val PHOTO_FILENAME = "reportIncidentPhoto"
        private const val FILE_PROVIDER_AUTHORITY =
            "com.ubb.citizen_u.ui.fragments.report.fileprovider"
    }

    private var _binding: FragmentReportIncidentBinding? = null
    val binding get() = _binding!!

    private lateinit var photoFile: File
    private val photoIncidentResultLauncher =
        registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.d(
                    TAG,
                    "photoIncidentResultLauncher: The incident report photo was taken successfully. " +
                            "Photo File Path is ${photoFile.absoluteFile}"
                )

                val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                binding.reportIncidentImageview.setImageBitmap(takenImage)


                FirebaseStorage.getInstance().getReference("images/incident_reports/${photoFile.name}")
                    .putFile(Uri.fromFile(photoFile))
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "The report incident has been sent successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e(
                                TAG,
                                "FirebaseStorage: An error has occurred: ${task.result?.error?.message}",
                            )
                            toastErrorMessage()
                        }
                    }
            }
        }

    private fun getPhotoFile(): File {
        val photoStorageDirectory =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(PHOTO_FILENAME, ".jpg", photoStorageDirectory)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportIncidentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            reportIncidentFragment = this@ReportIncidentFragment
        }
    }

    fun sendIncidentReport() {
        Log.d(TAG, "sendIncidentReport: Sending incident report...")
    }

    fun takePhoto() {
        Log.d(TAG, "takePhoto: Taking incident photo...")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        photoFile = getPhotoFile()
        val fileProvider =
            FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, photoFile)

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        try {
            photoIncidentResultLauncher.launch(takePictureIntent)
        } catch (exception: Exception) {
            Log.e(TAG, "takePhoto: Exception thrown: ${exception.message}")
            Toast.makeText(requireContext(), "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }
}