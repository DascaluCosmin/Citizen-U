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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.R
import com.ubb.citizen_u.data.model.Pdf
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.databinding.FragmentProjectProposalAttachmentBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.ui.viewmodels.ProjectProposalViewModel
import com.ubb.citizen_u.util.glide.ImageFiller
import com.ubb.citizen_u.util.isNull
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProjectProposalAttachmentFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ProjectProposalPdfFragment"
        private const val PDF_FILE_TYPE = "application/pdf"

        // TODO: This has to be clarified if .jpg is accepted as well
        private const val PHOTO_FILE_TYPE = "image/*"
    }

    private var _binding: FragmentProjectProposalAttachmentBinding? = null
    private val binding: FragmentProjectProposalAttachmentBinding get() = _binding!!

    private val args: ProjectProposalAttachmentFragmentArgs by navArgs()
    private val projectProposalViewModel: ProjectProposalViewModel by activityViewModels()
    private val citizenViewModel: CitizenViewModel by activityViewModels()

    private lateinit var currentFileType: String
    private var currentFileUri: Uri? = null

    private val uploadFileResultLauncher =
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectProposeProjectState() }
            }
        }
    }

    private suspend fun collectProposeProjectState() {
        projectProposalViewModel.proposeProjectState.collect {
            Log.d(TAG, "collectProposeProjectState: Collecting response $it...")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.e(
                        TAG,
                        "collectProposeProjectState: An error has occurred at proposing the project: ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    toastMessage(getString(R.string.SUCCESSFUL_PROPOSAL_PROJECT))

                    val action =
                        ProjectProposalAttachmentFragmentDirections.actionProjectProposalAttachmentFragmentToSignedInFragment(
                            citizenId = citizenViewModel.citizenId
                        )
                    findNavController().navigate(action)
                }
            }
        }
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
                    toastErrorMessage(getString(R.string.INVALID_ATTACHMENT_TITLE_TEXT_ERROR_MESSAGE))
                }
                currentFileUri.isNull() -> {
                    toastErrorMessage(getString(R.string.INVALID_PDF_URI_TEXT_ERROR_MESSAGE))
                }

                else -> {
                    projectProposalViewModel.addAttachment(Pdf().apply {
                        name = attachmentTitle
                        description = attachmentDescription
                        uri = currentFileUri
                    })
                    pdfTitleEdittext.editText?.text?.clear()
                    pdfDescriptionEdittext.editText?.text?.clear()
                    toastMessage(getString(R.string.SUCCESSFUL_ADDED_PDF))
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
                    toastErrorMessage(getString(R.string.INVALID_ATTACHMENT_TITLE_TEXT_ERROR_MESSAGE))
                }
                currentFileUri.isNull() -> {
                    toastErrorMessage(getString(R.string.INVALID_IMAGE_URI_TEXT_ERROR_MESSAGE))
                }

                else -> {
                    projectProposalViewModel.addAttachment(Photo().apply {
                        name = attachmentTitle
                        description = attachmentDescription
                        uri = currentFileUri
                    })
                    imageTitleEdittext.editText?.text?.clear()
                    imageDescriptionEdittext.editText?.text?.clear()
                    toastMessage(getString(R.string.SUCCESSFUL_ADDED_IMAGE))
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
            uploadFileResultLauncher.launch(intent)
        } catch (exception: Exception) {
            Log.e(TAG,
                "An exception has occurred at uploading the $fileType file: ${exception.message}")
            toastErrorMessage()
        }
    }

    fun goNext() {
        projectProposalViewModel.proposeProject(ProjectProposal(
            proposedBy = citizenViewModel.currentCitizen,
            proposedOn = Date(),
            category = args.projectCategory,
            title = args.projectTitle,
            motivation = args.projectMotivation,
            description = args.projectDescription,
            location = args.projectDescription,
        ))
    }
}