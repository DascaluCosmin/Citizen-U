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
import com.ubb.citizen_u.data.api.AddressApi
import com.ubb.citizen_u.databinding.FragmentSignedInBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.MainActivity
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.AuthenticationViewModel
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import com.ubb.citizen_u.ui.workers.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignedInFragment : Fragment() {

    companion object {
        const val TAG = "UBB-SignedInFragment"
    }

    private val citizenViewModel: CitizenViewModel by activityViewModels()
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()
    private val eventViewModel: EventViewModel by activityViewModels()

    private var _binding: FragmentSignedInBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val args: SignedInFragmentArgs by navArgs()

    @Inject
    lateinit var addressApi: AddressApi

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectCitizenState() }
                launch { collectSignOutState() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        citizenViewModel.getCitizen(args.citizenId)

        shouldGoToPeriodicEventDetails()?.let {
            Log.d(TAG, "onViewCreated: Navigating to the current periodic event: $it")

            eventViewModel.openCurrentPeriodicReleaseEventState()
            val action =
                SignedInFragmentDirections.actionSignedInFragmentToPublicReleaseEventDetailsFragment(
                    publicReleaseEventDetailsId = it
                )
            findNavController().navigate(action)
        }
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

    private suspend fun collectSignOutState() {
        authenticationViewModel.signOutState.collect {
            Log.d(TAG, "collectSignOutState: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.d(TAG, "collectSignOutState: An error has occurred ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    (requireActivity() as MainActivity).onBackPressedLogout()
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

    fun goToGenerateReports() {
        findNavController().navigate(R.id.action_signedInFragment_to_generateReportsReportedIncidentsFragment)
    }

    fun goToProposedProject() {
        findNavController().navigate(R.id.action_signedInFragment_to_projectProposalGeneralFragment)
    }

    fun goToPublicSpending() {
        findNavController().navigate(R.id.action_signedInFragment_to_publicSpendingFragment)
    }

    fun viewCitizenProposedProjects() {
        findNavController().navigate(R.id.action_signedInFragment_to_citizenProposedProjectsTransitionFragment)
    }

    fun viewOtherCitizensProposedProjects() {
        findNavController().navigate(R.id.action_signedInFragment_to_otherCitizensProposedProjectsTransitionFragment)
    }

    fun viewCitizenReportedIncidents() {
        findNavController().navigate(R.id.action_signedInFragment_to_citizenReportedIncidentsTransitionFragment)
    }

    fun viewOtherCitizensReportedIncidents() {
        findNavController().navigate(R.id.action_signedInFragment_to_otherCitizensReportedIncidentsTransitionFragment)
    }

    fun signOut() {
        authenticationViewModel.signOut()

//        Thread.sleep(3000L)
//        (requireActivity() as MainActivity).onBackPressedLogout()
    }

    private fun shouldGoToPeriodicEventDetails(): String? {
        if (eventViewModel.getCurrentPeriodicEventNotificationState() != NotificationWorker.PeriodicEventNotificationState.UNKNOWN) {
            return null
        }
        return args.periodicEventDetailsId
    }
}
