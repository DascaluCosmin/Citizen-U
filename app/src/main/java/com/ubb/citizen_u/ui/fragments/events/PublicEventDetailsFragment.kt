package com.ubb.citizen_u.ui.fragments.events

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentPublicEventDetailsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.getCurrentLanguage
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.SettingsConstants.DEFAULT_LANGUAGE
import com.ubb.citizen_u.util.glide.ImageFiller
import kotlinx.coroutines.launch


class PublicEventDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-PublicEventDetailsFragment"
        private const val DEFAULT_GOOGLE_SEARCH_SITE = "http://www.google.com/search?q="
    }

    private var _binding: FragmentPublicEventDetailsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by activityViewModels()
    private val args: PublicEventDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPublicEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            publicEventDetailsFragment = this@PublicEventDetailsFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetPublicEventDetailsState() }
            }
        }
    }

    private suspend fun collectGetPublicEventDetailsState() {
        eventViewModel.getPublicEventDetailsState.collect {
            Log.d(TAG, "collectGetPublicEventDetailsState: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                    binding.eventDetails.visibility = View.GONE
                }
                is Response.Error -> {
                    Log.e(TAG,
                        "collectGetPublicEventDetailsState: Error at collecting public event: ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    binding.eventDetails.visibility = View.VISIBLE

                    if (it.data != null) {
                        val language = requireContext().getCurrentLanguage()

                        binding.eventTitle.text = it.data.title[language]
                        binding.eventLocation.text = it.data.location
                        binding.eventAddress.text = it.data.address[language]
                        binding.eventCategory.text = it.data.category[language]
                        binding.eventContent.text = it.data.content[language]

                        val randomEventPhoto = it.data.chooseRandomEventPhoto()
                        if (randomEventPhoto == null) {
                            binding.eventImage.setImageResource(R.drawable.generic_town_hall_background)
                            binding.eventImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        } else {
                            ImageFiller.fill(
                                requireContext(),
                                binding.eventImage,
                                randomEventPhoto
                            )
                            randomEventPhoto.name?.let { photoName ->
                                binding.eventImage.contentDescription = photoName
                            }
                        }

                        it.data.startDate?.let { startDate ->
                            binding.eventStartDate.text =
                                DateFormatter.format(startDate)
                        }
                        it.data.endDate?.let { endDate ->
                            binding.dateSeparatorLabel.visibility = View.VISIBLE
                            binding.eventEndDate.text =
                                DateFormatter.format(endDate)
                        }
                    } else {
                        toastErrorMessage()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        eventViewModel.getPublicEventDetails(args.eventId)
    }

    fun goToEventOnlinePage() {
        Log.d(TAG, "goToEventOnlinePage: Going to event online page...")

        eventViewModel.currentPublicEventDetails?.let {
            try {
                val onlineEventIntent = Intent(Intent.ACTION_VIEW)
                val url = if (!it.websiteUrl.isNullOrEmpty()) {
                    it.websiteUrl!!
                } else {
                    "$DEFAULT_GOOGLE_SEARCH_SITE${it.title[DEFAULT_LANGUAGE]}"
                }
                onlineEventIntent.data = Uri.parse(url)
                startActivity(onlineEventIntent)
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "goToEventOnlinePage: Error at starting Open Event Website activity for event ${it.id}, url: ${it.websiteUrl}: $exception",
                )
            }
        }
    }
}