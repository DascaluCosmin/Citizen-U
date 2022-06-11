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
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.ui.viewmodels.EventViewModel
import com.ubb.citizen_u.ui.workers.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignedInFragment : Fragment() {

    companion object {
        const val TAG = "UBB-SignedInFragment"
    }

    private val citizenViewModel: CitizenViewModel by activityViewModels()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun viewPublicEventsList() {
//        lifecycleScope.launchWhenCreated {
//            val result = try {
//                addressApi.getAddress(46.7786231, 23.56184)
//            } catch (exception: IOException) {
//                Log.e(TAG,
//                    "IOException: there might be an internet connection issue: ${exception.message}")
//                toastErrorMessage("Internet Connection Issue")
//                return@launchWhenCreated
//            } catch (exception: HttpException) {
//                Log.e(TAG, "HttpException: unexpected response: ${exception.message()}")
//                toastErrorMessage()
//                return@launchWhenCreated
//            }
//            if (result.isSuccessful && result.body() != null) {
//                val address = result.body()
//                toastMessage(address?.address?.suburb.toString())
//            } else {
//                Log.e(TAG, "An error has occurred. The result is ${result.isSuccessful}")
//            }
//        }
        findNavController().navigate(R.id.action_signedInFragment_to_eventsListFragment)
    }

    fun viewPublicReleaseEventsList() {
        findNavController().navigate(R.id.action_signedInFragment_to_publicReleaseEventsListFragment)
    }

    fun goToReportIncident() {
        findNavController().navigate(R.id.action_signedInFragment_to_reportIncidentFragment)
    }

    private fun shouldGoToPeriodicEventDetails(): String? {
        if (eventViewModel.getCurrentPeriodicEventNotificationState() != NotificationWorker.PeriodicEventNotificationState.UNKNOWN) {
            // Workaround in order to reset the periodicEvent when coming back from the Details Screen
            return null
        }
        return args.periodicEventDetailsId
    }
}
