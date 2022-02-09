package com.ubb.citizen_u.ui.fragments.reports

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.databinding.FragmentReportIncidentBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.fragments.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.util.CitizenRequestConstants.SUCCESSFUL_REPORT_INCIDENT
import com.ubb.citizen_u.util.ValidationConstants.INVALID_REPORT_INCIDENT_DESCRIPTION_ERROR_MESSAGE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ReportIncidentFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ReportIncidentFragment"
        private const val PHOTO_FILENAME = "reportIncidentPhoto"
        private const val FILE_PROVIDER_AUTHORITY =
            "com.ubb.citizen_u.ui.fragments.reports.fileprovider"
    }

    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()

    private var _binding: FragmentReportIncidentBinding? = null
    val binding get() = _binding!!

    private val args: ReportIncidentFragmentArgs by navArgs()

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
                citizenRequestViewModel.saveIncidentPhoto(Uri.fromFile(photoFile))
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectReportIncidentState() }
            }
        }
    }

    private suspend fun collectReportIncidentState() {
        citizenRequestViewModel.addReportIncidentState.collect {
            Log.d(TAG, "collectReportIncidentState: Collecting response $it")
            when (it) {
                is Response.Error -> {
                    Log.d(TAG, "collectReportIncidentState: An error has occurred: ${it.message}")
                    toastErrorMessage(it.message)
                }
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    Toast.makeText(context, SUCCESSFUL_REPORT_INCIDENT, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sendIncidentReport() {
        Log.d(TAG, "sendIncidentReport: Sending incident report...")
        val description =
            binding.reportIncidentDescriptionEdittext.editText?.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(description) -> {
                Toast.makeText(
                    context,
                    INVALID_REPORT_INCIDENT_DESCRIPTION_ERROR_MESSAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                citizenRequestViewModel.reportIncident(
                    description = description,
                    citizenId = args.citizenId
                )
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
