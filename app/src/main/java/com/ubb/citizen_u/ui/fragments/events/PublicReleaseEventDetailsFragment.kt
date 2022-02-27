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
import com.ubb.citizen_u.databinding.FragmentPublicReleaseEventDetailsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.getCurrentLanguage
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PublicReleaseEventDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-PublicReleaseEventDetailsFragment"
    }

    private var _binding: FragmentPublicReleaseEventDetailsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by activityViewModels()
    private val args: PublicReleaseEventDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPublicReleaseEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            publicReleaseEventDetailsFragment = this@PublicReleaseEventDetailsFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetPublicReleaseEventDetailsState() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        eventViewModel.getPublicReleaseEventDetails(args.publicReleaseEventDetailsId)
    }

    private suspend fun collectGetPublicReleaseEventDetailsState() {
        eventViewModel.getPublicReleaseEventDetailsState.collect {
            Log.d(TAG, "collectGetPublicReleaseEventDetailsState: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                    binding.publicReleaseEventDetails.visibility = View.GONE
                }
                is Response.Error -> {
                    Log.e(TAG,
                        "collectGetPublicReleaseEventDetailsState: Error at collecting public release event: ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    Log.d(TAG,
                        "collectGetPublicReleaseEventDetailsState: Successfully collected public release event: $it")
                    binding.mainProgressbar.visibility = View.GONE
                    binding.publicReleaseEventDetails.visibility = View.VISIBLE

                    if (it.data != null) {
                        val language = requireContext().getCurrentLanguage()

                        binding.eventTitle.text = it.data.title[language]

                        val randomEventPhoto = it.data.chooseRandomEventPhoto()
                        ImageFiller.fill(
                            requireContext(),
                            binding.eventImage,
                            randomEventPhoto
                        )

                        it.data.publicationDate?.let { publicationDate ->
                            binding.eventPublicationDate.text =
                                DateFormatter.toEventFormat(publicationDate)
                        }
                    } else {
                        toastErrorMessage()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // TODO: Solve notification and then language switch bug
        eventViewModel.consumeCurrentPeriodicReleaseEventState()
    }
}