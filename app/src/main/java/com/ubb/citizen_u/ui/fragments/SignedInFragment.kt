package com.ubb.citizen_u.ui.fragments

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentSignedInBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignedInFragment : Fragment() {

    companion object {
        const val TAG = "UBB-SignedInFragment"
    }

    private val citizenViewModel: CitizenViewModel by activityViewModels()

    private var _binding: FragmentSignedInBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val args: SignedInFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignedInBinding.inflate(inflater, container, false)

        binding.apply {
            signedInFragment = this@SignedInFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shouldGoToPeriodicEventDetails()?.let {
            /**
            TODO: Note that currently this is susceptible to a bug
            due to the fact that upon logout -> login, the args might be still non-null.
             */
            Log.d(TAG, "onViewCreated: The current periodic event id is $it")
            val action =
                SignedInFragmentDirections.actionSignedInFragmentToPublicReleaseEventDetailsFragment(
                    publicReleaseEventId = it
                )
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectCitizenState() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        citizenViewModel.getCitizen(args.citizenId)
    }

    private suspend fun collectCitizenState() {
        citizenViewModel.getCitizenState.collect {
            Log.d(TAG, "collectCitizenState: Collecting response $it")
            when (it) {
                is Response.Error -> {
                    Log.d(TAG, "collectCitizenState: An error has occurred ${it.message}")
                    binding.apply {
                        mainLayout.visibility = View.GONE
                        mainProgressbar.visibility = View.GONE
                    }
                    toastErrorMessage()
                }
                Response.Loading -> {
                    binding.apply {
                        mainLayout.visibility = View.GONE
                        mainProgressbar.visibility = View.VISIBLE
                    }
                }
                is Response.Success -> {
                    Log.d(TAG, "collectCitizenState: Successfully collected ${it.data}")
                    if (it.data == null) {
                        Log.d(TAG, "collectCitizenState: An error has occurred. Result is null")
                        toastErrorMessage()
                        return@collect
                    }
                    binding.apply {
                        mainLayout.visibility = View.VISIBLE
                        mainProgressbar.visibility = View.GONE

                        welcomeTextview.text = getString(
                            R.string.signed_in_your_account_textview_params,
                            it.data.firstName
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun viewPublicEventsList() {
        findNavController().navigate(R.id.action_signedInFragment_to_eventsListFragment)
    }

    fun viewPublicReleaseEventsList() {
        findNavController().navigate(R.id.action_signedInFragment_to_publicReleaseEventsListFragment)
    }

    fun goToReportIncident() {
        findNavController().navigate(R.id.action_signedInFragment_to_reportIncidentFragment)
    }

    private fun shouldGoToPeriodicEventDetails(): String? {
        return args.periodicEventDetailsId
    }
}
