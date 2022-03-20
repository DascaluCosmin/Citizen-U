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
import com.ubb.citizen_u.data.model.citizens.requests.Comment
import com.ubb.citizen_u.databinding.FragmentReportedIncidentDetailsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.util.CitizenRequestConstants.SUCCESSFUL_ADD_COMMENT_TO_INCIDENT
import com.ubb.citizen_u.util.ConfigurationConstants.IMAGE_CAROUSEL_NUMBER_OF_SECONDS
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.ValidationConstants.INVALID_INCIDENT_COMMENT_TEXT_ERROR_MESSAGE
import com.ubb.citizen_u.util.glide.ImageFiller
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

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
                    toastMessage(SUCCESSFUL_ADD_COMMENT_TO_INCIDENT)
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
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    binding.reportedIncidentImage.visibility = View.VISIBLE
                    binding.reportedIncidentDetails.visibility = View.VISIBLE

                    it.data?.run {
                        binding.reportedIncidentHeadline.text = headline
                        binding.reportedIncidentDescription.text = description
                        binding.reportedIncidentAddress.text = address
                        binding.reportedIncidentPostedBy.text = citizen?.getFullName()
                        binding.reportedIncidentStatus.text = status.toString().uppercase()

                        sentDate?.let { sentDate ->
                            binding.reportedIncidentPostedOn.text =
                                DateFormatter.toEventFormat(sentDate)
                        }

                        imageCarouselRunnable = Runnable {
                            Log.d(TAG, "Image Carousel running...")
                            ImageFiller.fill(requireContext(),
                                binding.reportedIncidentImage,
                                citizenRequestViewModel.getNextIncidentPhoto())
                            handler.postDelayed(imageCarouselRunnable,
                                IMAGE_CAROUSEL_NUMBER_OF_SECONDS)
                        }

                        handler.postDelayed(imageCarouselRunnable,
                            0L) // The first image should be displayed immediately

                        // TODO: Image carousel to be implemented
//                        photos.let { incidentPhotos ->
//                            if (incidentPhotos.isNotEmpty()) {
//                                incidentPhotos[0]?.let { incidentPhoto ->
//                                    ImageFiller.fill(
//                                        requireContext(),
//                                        binding.reportedIncidentImage,
//                                        incidentPhoto
//                                    )
//                                }
//                            }
//                        }

                        if (citizen?.id == citizenViewModel.currentCitizen.id) {
                            binding.addIncidentCommentTextfield.visibility = View.GONE
                            binding.addCommentButton.visibility = View.GONE
                        }

                        if (!comments.isNullOrEmpty()) {
                            binding.incidentCommentLayout.visibility = View.VISIBLE
                            binding.reportedIncidentCommentsLabel.visibility = View.VISIBLE

                            binding.incidentCommentText.text = comments[0]?.text
                        } else {
                            binding.incidentCommentLayout.visibility = View.GONE
                            binding.reportedIncidentCommentsLabel.visibility = View.GONE
                        }
                    }
                }
            }
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
                toastMessage(INVALID_INCIDENT_COMMENT_TEXT_ERROR_MESSAGE)
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
}