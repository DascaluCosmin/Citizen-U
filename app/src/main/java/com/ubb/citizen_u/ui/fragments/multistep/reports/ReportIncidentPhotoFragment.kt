package com.ubb.citizen_u.ui.fragments.multistep.reports

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentReportIncidentPhotoBinding
import com.ubb.citizen_u.ui.util.getRotatedBitmap
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.util.ValidationConstants.INVALID_REPORT_INCIDENT_PHOTO_ERROR_MESSAGE
import java.io.File

class ReportIncidentPhotoFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ReportIncidentPhotoFragment"
        private const val PHOTO_FILENAME = "reportIncidentPhoto"
        private const val FILE_PROVIDER_AUTHORITY =
            "com.ubb.citizen_u.ui.fragments.multistep.reports.fileprovider"
    }

    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()
    private val citizenViewModel: CitizenViewModel by activityViewModels()
    private val args: ReportIncidentPhotoFragmentArgs by navArgs()

    private var _binding: FragmentReportIncidentPhotoBinding? = null
    val binding get() = _binding!!

    private lateinit var photoFile: File
    private val photoIncidentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.d(
                    TAG,
                    "photoIncidentResultLauncher: The incident report photo was taken successfully. " +
                            "Photo File Path is ${photoFile.absoluteFile}"
                )
                citizenRequestViewModel.addIncidentPhoto(Uri.fromFile(photoFile))
            }
        }

    private fun setImage(path: String?) {
        val imageBitmap = BitmapFactory.decodeFile(path)
        binding.apply {
            reportIncidentImageview.setImageBitmap(getRotatedBitmap(path, imageBitmap))
            if (path.isNullOrEmpty()) {
                removeIncidentPhotoButton.visibility = View.GONE
                reportIncidentPhotoHint.visibility = View.VISIBLE
                takePhotoButton.text = getString(R.string.take_photo_button_text)
            } else {
                removeIncidentPhotoButton.visibility = View.VISIBLE
                reportIncidentPhotoHint.visibility = View.GONE
                takePhotoButton.text = getString(R.string.take_another_photo_button_text)
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
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportIncidentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            reportIncidentPhotoFragment = this@ReportIncidentPhotoFragment
        }

        citizenRequestViewModel.listIncidentPhotoUriLiveData.observe(viewLifecycleOwner) {
            setImage(it.lastOrNull()?.path)
        }
    }

    override fun onStart() {
        super.onStart()
        citizenRequestViewModel.getCitizenReportedIncidents(citizenViewModel.citizenId)
    }

    fun takePhoto() {
        Log.d(TAG, "takePhoto: Taking incident photo...")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        photoFile = getPhotoFile()
        val fileProvider =
            FileProvider.getUriForFile(
                requireContext(),
                FILE_PROVIDER_AUTHORITY, photoFile
            )

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        try {
            photoIncidentResultLauncher.launch(takePictureIntent)
        } catch (exception: Exception) {
            Log.e(TAG, "takePhoto: Exception thrown: ${exception.message}")
            Toast.makeText(requireContext(), "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeLatestPhoto() {
        Log.d(TAG, "removeLatestPhoto: Removing latest photo...")
        citizenRequestViewModel.removeLatestPhoto()
    }

    fun goNext() {
        Log.d(TAG, "Going next in multistep report incident...")
        when {
            citizenRequestViewModel.listIncidentPhotoUriLiveData.value.isNullOrEmpty() -> {
                Log.d(TAG, "There are no incident photos")
                toastErrorMessage(
                    INVALID_REPORT_INCIDENT_PHOTO_ERROR_MESSAGE
                )
            }
            else -> {
                val action =
                    ReportIncidentPhotoFragmentDirections.actionReportIncidentPhotoFragmentToReportIncidentMapFragment(
                        incidentDescription = args.incidentDescription,
                        incidentHeadline = args.incidentHeadline,
                    )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}