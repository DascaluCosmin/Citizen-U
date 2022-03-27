package com.ubb.citizen_u.ui.fragments.multistep.projectproposals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.databinding.FragmentProjectProposalPdfBinding
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN
import com.ubb.citizen_u.util.glide.ImageFiller
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectProposalPdfFragment : Fragment() {

    companion object {
        const val TAG = "UBB-ProjectProposalPdfFragment"
        const val PDF_FILE_TYPE = "application/pdf"

        // TODO: This has to be clarified if .jpg is accepted as well
        const val PHOTO_FILE_TYPE = "image/*"
    }

    private var _binding: FragmentProjectProposalPdfBinding? = null
    private val binding: FragmentProjectProposalPdfBinding get() = _binding!!

    private lateinit var currentFileType: String

    private val uploadPdfResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val uri = intent.data
                    Log.d(TAG,
                        "The $currentFileType file type has been upload successfully in the application's memory. Uri = $uri")

                    binding.apply {
                        uploadFilePlaceholder.visibility = View.GONE
                        when (currentFileType) {
                            PDF_FILE_TYPE -> {
                                projectProposalPdf.fromUri(uri).load()
                                imagePanel.visibility = View.GONE
                                pdfPanel.visibility = View.VISIBLE
                            }

                            PHOTO_FILE_TYPE -> {
                                ImageFiller.fill(
                                    requireContext(),
                                    binding.projectProposalPhoto,
                                    uri
                                )
                                imagePanel.visibility = View.VISIBLE
                                pdfPanel.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProjectProposalPdfBinding.inflate(inflater, container, false)

        binding.apply {
            projectProposalPdfFragment = this@ProjectProposalPdfFragment
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun uploadPdfFile() {
        uploadFile(PDF_FILE_TYPE)
    }

    fun uploadPhotoFile() {
        uploadFile(PHOTO_FILE_TYPE)
    }

    private fun uploadFile(fileType: String) {
        Log.d(TAG, "uploadFile: Uploading $fileType file...")
        currentFileType = fileType

        val intent = Intent()
        intent.type = fileType
        intent.action = Intent.ACTION_GET_CONTENT
        try {
            uploadPdfResultLauncher.launch(intent)
        } catch (exception: Exception) {
            Log.e(TAG,
                "An exception has occurred at uploading the $fileType file: ${exception.message}")
            toastMessage(DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN)
        }
    }
}