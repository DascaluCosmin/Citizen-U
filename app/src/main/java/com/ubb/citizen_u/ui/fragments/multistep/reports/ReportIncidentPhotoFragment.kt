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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.ubb.citizen_u.R
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.databinding.FragmentReportIncidentPhotoBinding
import com.ubb.citizen_u.ui.model.PhotoWithSource
import com.ubb.citizen_u.ui.model.Source
import com.ubb.citizen_u.ui.util.getRotatedBitmap
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.util.CitizenRequestConstants.DEFAULT_INCIDENT_CATEGORY
import com.ubb.citizen_u.util.glide.ImageFiller
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ReportIncidentPhotoFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ReportIncidentPhotoFragment"
        private const val PHOTO_FILENAME = "reportIncidentPhoto"
        private const val FILE_PROVIDER_AUTHORITY =
            "com.ubb.citizen_u.ui.fragments.multistep.reports.fileprovider"

        private const val PHOTO_FILE_TYPE = "image/*"
    }

    @Inject
    lateinit var labeler: ImageLabeler

    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()
    private val citizenViewModel: CitizenViewModel by activityViewModels()
    private val args: ReportIncidentPhotoFragmentArgs by navArgs()

    private var _binding: FragmentReportIncidentPhotoBinding? = null
    val binding get() = _binding!!

    private lateinit var photoFile: File
    private val photoIncidentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d(
                        TAG,
                        "The incident report photo was taken successfully. Photo File Path is ${photoFile.absoluteFile}"
                    )
                    val predictedCategory = getPredictedCategoryForPhoto(photoFile.toUri())

                    citizenRequestViewModel.addIncidentPhoto(PhotoWithSource(
                        photo = Photo(
                            category = predictedCategory,
                            uri = Uri.fromFile(photoFile)
                        ),
                        source = Source.CAMERA
                    ))
                }
            }
        }
    private val uploadPhotoIncidentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            lifecycleScope.launch(Dispatchers.IO) {
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        Log.d(TAG, "The incident report photo has been uploaded successfully")
                        val predictedCategory = getPredictedCategoryForPhoto(uri)

                        Log.d(TAG, "The predicted category for the image is: $predictedCategory")
                        citizenRequestViewModel.addIncidentPhoto(PhotoWithSource(
                            photo = Photo(
                                category = predictedCategory,
                                uri = uri,
                            ),
                            source = Source.UPLOAD
                        ))
                    }
                }
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

    private suspend fun getPredictedCategoryForPhoto(uri: Uri): String {
        val labels = labeler.process(InputImage.fromFilePath(requireContext(), uri)).await()
        return if (labels.isEmpty()) DEFAULT_INCIDENT_CATEGORY else labels[0].text
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

        citizenRequestViewModel.listIncidentPhotoWithSourceLiveData.observe(viewLifecycleOwner) { list ->
            val lastElement = list.lastOrNull()
            if (lastElement != null) {
                setImage(lastElement)
            } else {
                binding.apply {
                    reportIncidentImageview.setImageBitmap(BitmapFactory.decodeFile(null))
                    removeIncidentPhotoButton.visibility = View.GONE
                    reportIncidentPhotoHint.visibility = View.VISIBLE
                    takePhotoButton.text = getString(R.string.take_photo_button_text)
                }
            }

            list.lastOrNull()?.let { mostRecentPhoto ->
                setImage(mostRecentPhoto)
            }
        }
    }

    private fun setImage(photoWithSource: PhotoWithSource) {
        binding.apply {
            removeIncidentPhotoButton.visibility = View.VISIBLE
            reportIncidentPhotoHint.visibility = View.GONE
            takePhotoButton.text = getString(R.string.take_another_photo_button_text)
        }

        val photoUri = photoWithSource.photo?.uri
        when (photoWithSource.source) {
            Source.UPLOAD -> ImageFiller.fill(
                requireContext(),
                binding.reportIncidentImageview,
                photoUri
            )
            Source.CAMERA -> {
                val path = photoUri!!.path
                val imageBitmap = BitmapFactory.decodeFile(path)
                binding.reportIncidentImageview.setImageBitmap(getRotatedBitmap(path, imageBitmap))
            }
        }
    }

    fun takePhoto() {
        Log.d(TAG, "Taking incident photo...")
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

    fun uploadPhoto() {
        Log.d(TAG, "Uploading incident photo...")

        val intent = Intent()
        intent.type = PHOTO_FILE_TYPE
        intent.action = Intent.ACTION_GET_CONTENT
        try {
            uploadPhotoIncidentResultLauncher.launch(intent)
        } catch (exception: Exception) {
            Log.e(TAG, "Error at uploading incident photo: ${exception.message}")
            toastErrorMessage()
        }
    }

    fun removeLatestPhoto() {
        Log.d(TAG, "removeLatestPhoto: Removing latest photo...")
        citizenRequestViewModel.removeLatestPhoto()
    }

    fun goNext() {
        Log.d(TAG, "Going next in multistep report incident...")
        when {
            citizenRequestViewModel.listIncidentPhotoWithSourceLiveData.value.isNullOrEmpty() -> {
                Log.d(TAG, "There are no incident photos")
                toastErrorMessage(
                    getString(R.string.INVALID_REPORT_INCIDENT_PHOTO_ERROR_MESSAGE)
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