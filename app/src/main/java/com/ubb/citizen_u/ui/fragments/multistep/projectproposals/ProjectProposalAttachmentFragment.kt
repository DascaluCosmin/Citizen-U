package com.ubb.citizen_u.ui.fragments.multistep.projectproposals

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.data.model.Pdf
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.databinding.FragmentProjectProposalAttachmentBinding
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.ui.viewmodels.ProjectProposalViewModel
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN
import com.ubb.citizen_u.util.ProjectProposalConstants.SUCCESSFUL_ADDED_IMAGE
import com.ubb.citizen_u.util.ProjectProposalConstants.SUCCESSFUL_ADDED_PDF
import com.ubb.citizen_u.util.ValidationConstants.INVALID_ATTACHMENT_TITLE_TEXT_ERROR_MESSAGE
import com.ubb.citizen_u.util.ValidationConstants.INVALID_IMAGE_URI_TEXT_ERROR_MESSAGE
import com.ubb.citizen_u.util.ValidationConstants.INVALID_PDF_URI_TEXT_ERROR_MESSAGE
import com.ubb.citizen_u.util.glide.ImageFiller
import com.ubb.citizen_u.util.isNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProjectProposalAttachmentFragment : Fragment() {

    companion object {
        const val TAG = "UBB-ProjectProposalPdfFragment"
        const val PDF_FILE_TYPE = "application/pdf"

        // TODO: This has to be clarified if .jpg is accepted as well
        const val PHOTO_FILE_TYPE = "image/*"
    }

    private var _binding: FragmentProjectProposalAttachmentBinding? = null
    private val binding: FragmentProjectProposalAttachmentBinding get() = _binding!!

    private val args: ProjectProposalAttachmentFragmentArgs by navArgs()
    private val projectProposalViewModel: ProjectProposalViewModel by activityViewModels()
    private val citizenViewModel: CitizenViewModel by activityViewModels()

    private lateinit var currentFileType: String
    private var currentFileUri: Uri? = null

    private val uploadPdfResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    val uri = intent.data
                    currentFileUri = uri
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
        _binding = FragmentProjectProposalAttachmentBinding.inflate(inflater, container, false)

        binding.apply {
            projectProposalPdfFragment = this@ProjectProposalAttachmentFragment
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

    fun addPdfFile() {
        binding.run {
            val attachmentTitle = pdfTitleEdittext.editText?.text.toString().trim { it <= ' ' }
            val attachmentDescription =
                pdfDescriptionEdittext.editText?.text.toString().trim { it <= ' ' }
            when {
                TextUtils.isEmpty(attachmentTitle) -> {
                    toastErrorMessage(INVALID_ATTACHMENT_TITLE_TEXT_ERROR_MESSAGE)
                }
                currentFileUri.isNull() -> {
                    toastErrorMessage(INVALID_PDF_URI_TEXT_ERROR_MESSAGE)
                }

                else -> {
                    projectProposalViewModel.addAttachment(Pdf().apply {
                        name = attachmentTitle
                        description = attachmentDescription
                        uri = currentFileUri
                    })
                    toastMessage(SUCCESSFUL_ADDED_PDF)
                }
            }
        }
    }

    fun addImageFile() {
        binding.run {
            val attachmentTitle = imageTitleEdittext.editText?.text.toString().trim { it <= ' ' }
            val attachmentDescription =
                imageDescriptionEdittext.editText?.text.toString().trim { it <= ' ' }
            when {
                TextUtils.isEmpty(attachmentTitle) -> {
                    toastErrorMessage(INVALID_ATTACHMENT_TITLE_TEXT_ERROR_MESSAGE)
                }
                currentFileUri.isNull() -> {
                    toastErrorMessage(INVALID_IMAGE_URI_TEXT_ERROR_MESSAGE)
                }

                else -> {
                    projectProposalViewModel.addAttachment(Photo().apply {
                        name = attachmentTitle
                        description = attachmentDescription
                        uri = currentFileUri
                    })
                    toastMessage(SUCCESSFUL_ADDED_IMAGE)
                }
            }
        }
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

    fun goNext() {
        projectProposalViewModel.proposeProject(ProjectProposal(
            proposedBy = citizenViewModel.currentCitizen
        )
        )
    }
}