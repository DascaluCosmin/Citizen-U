package com.ubb.citizen_u.ui.fragments.events

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
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.events.Event
import com.ubb.citizen_u.databinding.FragmentPublicEventDetailsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class PublicEventDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-PublicEventDetailsFragment"
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
                        binding.eventTitle.text = it.data.title
                        binding.eventLocation.text = it.data.location
                        binding.eventAddress.text = it.data.address

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
}