package com.ubb.citizen_u.ui.fragments.reports

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
import com.ubb.citizen_u.R
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.databinding.FragmentReportedIncidentDetailsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.util.ConfigurationConstants.IMAGE_CAROUSEL_NUMBER_OF_SECONDS
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalCoroutinesApi
class ReportedIncidentDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ReportedIncidentDetailsFragment"
    }

    private lateinit var imageCarouselRunnable: Runnable

    private var _binding: FragmentReportedIncidentDetailsBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler()
    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()
    private val citizenViewModel: CitizenViewModel by activityViewModels()
    private val args: ReportedIncidentDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportedIncidentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            reportedIncidentDetailsFragment = this@ReportedIncidentDetailsFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetReportedIncidentState() }
                launch { collectAddCommentToIncidentState() }
            }
        }
    }

    private suspend fun collectAddCommentToIncidentState() {
        citizenRequestViewModel.addCommentToIncidentState.collect {
            Log.d(TAG, "collectAddCommentToIncidentState: Collecting response $it")
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

                    binding.addIncidentCommentTextfield.editText?.text?.clear()
                    toastMessage(getString(R.string.SUCCESSFUL_ADD_COMMENT))
                    getCurrentComment(it.data.comments)
                }
            }
        }
    }

    private suspend fun collectGetReportedIncidentState() {
        citizenRequestViewModel.getReportedIncidentState.collect {
            Log.d(TAG, "collectGetReportedIncidentState: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                    binding.reportedIncidentImage.visibility = View.GONE
                    binding.reportedIncidentDetails.visibility = View.GONE
                }
                is Response.Error -> {
                    binding.mainProgressbar.visibility = View.GONE
                    binding.reportedIncidentImage.visibility = View.GONE
                    binding.reportedIncidentDetails.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    binding.reportedIncidentImage.visibility = View.VISIBLE
                    binding.reportedIncidentDetails.visibility = View.VISIBLE

                    it.data?.run {
                        binding.reportedIncidentHeadline.text = headline
                        binding.reportedIncidentCategory.text = category
                            ?.replaceFirstChar { character ->
                                character.uppercase()
                            }
                        binding.reportedIncidentDescription.text = description
                        binding.reportedIncidentAddress.text = address
                        binding.reportedIncidentPostedBy.text = citizen?.getFullName()
                        binding.reportedIncidentStatus.text = status.toString().lowercase()
                            .replace("_", " ")
                            .replaceFirstChar { incidentStatus ->
                                if (incidentStatus.isLowerCase()) incidentStatus.titlecase(Locale.getDefault()) else incidentStatus.toString()
                            }

                        sentDate?.let { sentDate ->
                            binding.reportedIncidentPostedOn.text =
                                DateFormatter.format(sentDate)
                        }

                        imageCarouselRunnable = Runnable {
                            Log.d(TAG, "Image Carousel running...")
                            ImageFiller.fill(requireContext(),
                                binding.reportedIncidentImage,
                                citizenRequestViewModel.getNextIncidentPhoto())

                            if (photos.size > 1) {
                                handler.postDelayed(imageCarouselRunnable,
                                    IMAGE_CAROUSEL_NUMBER_OF_SECONDS)
                            }
                        }

                        handler.postDelayed(imageCarouselRunnable,
                            0L) // The first image should be displayed immediately

                        if (citizen?.id == citizenViewModel.currentCitizen.id) {
                            binding.addIncidentCommentTextfield.visibility = View.GONE
                            binding.addCommentButton.visibility = View.GONE
                        }

                        getCurrentComment(comments)
                    }
                }
            }
        }
    }

    private fun getCurrentComment(comments: List<Comment?>) {
        if (!comments.isNullOrEmpty()) {
            binding.incidentCommentLayout.visibility = View.VISIBLE
            binding.reportedIncidentCommentsLabel.visibility = View.VISIBLE

            if (comments.size == 1) {
                binding.nextReportCommentButton.visibility = View.GONE
                binding.previousReportCommentButton.visibility = View.GONE
            } else {
                binding.nextReportCommentButton.visibility = View.VISIBLE
                binding.previousReportCommentButton.visibility = View.VISIBLE
            }

            getNextCommentToIncident()
        } else {
            binding.incidentCommentLayout.visibility = View.GONE
            binding.reportedIncidentCommentsLabel.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        citizenRequestViewModel.getReportedIncident(
            citizenId = args.citizenId,
            incidentId = args.incidentId
        )
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(imageCarouselRunnable)
    }

    fun addIncidentComment() {
        val commentText =
            binding.addIncidentCommentTextfield.editText?.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(commentText) -> {
                toastMessage(getString(R.string.INVALID_COMMENT_TEXT_ERROR_MESSAGE))
            }

            else -> {
                val currentCitizen = citizenViewModel.currentCitizen
                val comment = Comment(
                    text = commentText,
                    postedOn = Date(),
                    userFirstName = currentCitizen.firstName,
                    userLastName = currentCitizen.lastName,
                    userId = currentCitizen.id,
                )
                citizenRequestViewModel.addCommentToCurrentIncident(comment)
            }
        }
    }

    fun getPreviousCommentToIncident() {
        showCurrentCommentToIncident(citizenRequestViewModel.getPreviousIncidentComment())
    }

    fun getNextCommentToIncident() {
        showCurrentCommentToIncident(citizenRequestViewModel.getNextIncidentComment())
    }

    private fun showCurrentCommentToIncident(comment: Comment?) {
        comment?.let {
            binding.incidentCommentText.text = comment.text
            binding.incidentCommentPostedBy.text = comment.getUserFullName()
            comment.postedOn?.let { postedOn ->
                binding.incidentCommentPostedOn.text =
                    DateFormatter.format(postedOn)
            }
        }
    }
}