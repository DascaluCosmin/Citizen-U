package com.ubb.citizen_u.ui.fragments.multistep.projectproposals

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.databinding.FragmentProposedProjectsDetailsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.ui.viewmodels.ProjectProposalViewModel
import com.ubb.citizen_u.util.CitizenRequestConstants.SUCCESSFUL_ADD_COMMENT
import com.ubb.citizen_u.util.ConfigurationConstants.IMAGE_CAROUSEL_NUMBER_OF_SECONDS
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.ValidationConstants.INVALID_COMMENT_TEXT_ERROR_MESSAGE
import com.ubb.citizen_u.util.glide.ImageFiller
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalCoroutinesApi
class ProposedProjectsDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ProposedProjectsDetailsFragment"
    }

    private lateinit var imageCarouselRunnable: Runnable

    private var _binding: FragmentProposedProjectsDetailsBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler()
    private val citizenViewModel: CitizenViewModel by activityViewModels()
    private val projectProposalViewModel: ProjectProposalViewModel by activityViewModels()
    private val args: ProposedProjectsDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProposedProjectsDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            proposedProjectsDetailsFragment = this@ProposedProjectsDetailsFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetProjectProposalState() }
                launch { collectAddCommentToProjectProposalState() }
            }
        }
    }

    private suspend fun collectGetProjectProposalState() {
        projectProposalViewModel.getProposedProjectState.collect {
            Log.d(TAG, "collectGetProjectProposalState: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                    binding.proposedProjectImage.visibility = View.GONE
                    binding.proposedProjectDetails.visibility = View.GONE
                }
                is Response.Error -> {
                    binding.mainProgressbar.visibility = View.GONE
                    binding.proposedProjectImage.visibility = View.GONE
                    binding.proposedProjectDetails.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    binding.proposedProjectImage.visibility = View.VISIBLE
                    binding.proposedProjectDetails.visibility = View.VISIBLE

                    it.data?.run {
                        binding.proposedProjectTitle.text = title
                        binding.proposedProjectCategory.text = category
                            ?.replaceFirstChar { character ->
                                character.uppercase()
                            }
                        binding.proposedProjectDescription.text = description
                        binding.proposedProjectMotivation.text = motivation
                        binding.proposedProjectLocation.text = location
                        binding.proposedProjectPostedBy.text = proposedBy?.getFullName()
                        binding.proposedProjectNumberOfVotes.text = numberOfVotes.toString()

                        proposedOn?.let { proposedOnDate ->
                            binding.proposedProjectPostedOn.text =
                                DateFormatter.format(proposedOnDate)
                        }

                        imageCarouselRunnable = Runnable {
                            Log.d(TAG, "Image Carousel running...")
                            ImageFiller.fill(requireContext(),
                                binding.proposedProjectImage,
                                projectProposalViewModel.getNextProjectProposalPhoto())

                            if (photos.size > 1) {
                                handler.postDelayed(imageCarouselRunnable,
                                    IMAGE_CAROUSEL_NUMBER_OF_SECONDS)
                            }
                        }

                        handler.postDelayed(imageCarouselRunnable, 0L)

                        if (proposedBy?.id == citizenViewModel.currentCitizen.id) {
                            binding.addProjectProposalCommentTextfield.visibility = View.GONE
                            binding.addCommentButton.visibility = View.GONE
                        }

                        getCurrentComment(comments)
                    }
                }
            }
        }
    }

    private suspend fun collectAddCommentToProjectProposalState() {
        projectProposalViewModel.addCommentToProjectProposalState.collect {
            Log.d(TAG, "collectAddCommentToProjectProposalState: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE

                    binding.addProjectProposalCommentTextfield.editText?.text?.clear()
                    toastErrorMessage(SUCCESSFUL_ADD_COMMENT)
                    getCurrentComment(it.data.comments)
                }
            }
        }
    }

    private fun getCurrentComment(comments: List<Comment?>) {
        if (!comments.isNullOrEmpty()) {
            binding.proposedProjectCommentLayout.visibility = View.VISIBLE
            binding.proposedProjectCommentsLabel.visibility = View.VISIBLE

            if (comments.size == 1) {
                binding.nextProposalCommentButton.visibility = View.GONE
                binding.previousProposalCommentButton.visibility = View.GONE
            } else {
                binding.nextProposalCommentButton.visibility = View.VISIBLE
                binding.previousProposalCommentButton.visibility = View.VISIBLE
            }

            getNextCommentToProjectProposal()
        } else {
            binding.proposedProjectCommentLayout.visibility = View.GONE
            binding.proposedProjectCommentsLabel.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        projectProposalViewModel.getProposedProject(
            citizenId = args.citizenId,
            proposedProjectId = args.projectProposalId
        )
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(imageCarouselRunnable)
    }

    fun addProjectProposalComment() {
        val commentText =
            binding.addProjectProposalCommentTextfield.editText?.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(commentText) -> {
                toastMessage(INVALID_COMMENT_TEXT_ERROR_MESSAGE)
            }

            else -> {
                val currentCitizen = citizenViewModel.currentCitizen
                val comment = Comment(
                    text = commentText,
                    postedOn = Date(),
                    userFirstName = currentCitizen.firstName,
                    userLastName = currentCitizen.lastName,
                    userId = currentCitizen.id
                )
                projectProposalViewModel.addCommentToCurrentProposedProject(comment)
            }
        }
    }

    fun getPreviousCommentToProjectProposal() {
        showCurrentCommentToProposedProject(projectProposalViewModel.getPreviousProposedProjectComment())
    }

    fun getNextCommentToProjectProposal() {
        showCurrentCommentToProposedProject(projectProposalViewModel.getNextProposedProjectComment())
    }

    private fun showCurrentCommentToProposedProject(comment: Comment?) {
        comment?.let {
            binding.proposedProjectCommentText.text = comment.text
            binding.proposedProjectCommentPostedBy.text = comment.getUserFullName()
            comment.postedOn?.let { postedOn ->
                binding.proposedProjectPostedOn.text = DateFormatter.format(postedOn)
            }
        }
    }

    fun viewDocuments() {

    }
}