package com.ubb.citizen_u.ui.fragments.events

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
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
import androidx.preference.PreferenceManager
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.events.Event
import com.ubb.citizen_u.databinding.FragmentPublicEventDetailsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.SettingsConstants.DEFAULT_LANGUAGE
import com.ubb.citizen_u.util.SettingsConstants.LANGUAGE_SETTINGS_KEY
import com.ubb.citizen_u.util.glide.ImageFiller
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class PublicEventDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-PublicEventDetailsFragment"
        private const val DEFAULT_GOOGLE_SEARCH_SITE = "http://www.google.com/search?q="
    }

    private lateinit var settingsPreferences: SharedPreferences

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

        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    private suspend fun collectGetPublicEventDetailsState() {
        eventViewModel.getPublicEventDetailsState.collect {
            Log.d(TAG, "collectGetPublicEventDetailsState: Collecting response $it")
            when (it) {
                is Response.Error -> {
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                    binding.eventDetails.visibility = View.GONE
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    binding.eventDetails.visibility = View.VISIBLE

                    if (it.data != null) {
                        val language = settingsPreferences
                            .getString(LANGUAGE_SETTINGS_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE

                        binding.eventTitle.text = it.data.title[language]
                        binding.eventLocation.text = it.data.location
                        binding.eventAddress.text = it.data.address[language]

                        val randomEventPhoto = chooseRandomEventPhoto(it.data)
                        ImageFiller.fill(
                            requireContext(),
                            binding.eventImage,
                            randomEventPhoto
                        )
                        it.data.startDate?.let { startDate ->
                            binding.eventStartDate.text =
                                DateFormatter.toEventFormat(startDate)
                        }
                        it.data.endDate?.let { endDate ->
                            binding.dateSeparatorLabel.visibility = View.VISIBLE
                            binding.eventEndDate.text =
                                DateFormatter.toEventFormat(endDate)
                        }
                    } else {
                        toastErrorMessage()
                    }
                }
            }
        }
    }

    private fun chooseRandomEventPhoto(event: Event): Photo? {
        val randomIndex = Random().nextInt(event.photos.size)
        return event.photos[randomIndex]
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
                    "$DEFAULT_GOOGLE_SEARCH_SITE${it.title}"
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