package com.ubb.citizen_u.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentSignedInMockupBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.viewmodels.AuthenticationViewModel
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignedInMockupFragment : Fragment() {

    companion object {
        const val TAG = "UBB-SignedInMockupFragment"
    }

    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()
    private val citizenViewModel: CitizenViewModel by activityViewModels()

    private var _binding: FragmentSignedInMockupBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val args: SignedInMockupFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backButtonCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backButtonCallBack)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignedInMockupBinding.inflate(inflater, container, false)

        binding.apply {
            signedInMockupFragment = this@SignedInMockupFragment
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
        citizenViewModel.getCitizen(args.connectedUserId)
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
                            R.string.signed_in_mockup_your_account_textview_params,
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

    /**
     * TODO: Improve the signOut. Scenarios:
     * - The apps closes unexpectedly.
     *      - If you call signOut on the onStop callback, watch for Configuration Changes, e.g. Device Rotation
     * - The User uses the Home button. Question: what happens then?
     * - The User presses the Back button. - DONE
     */
    fun signOut() {
        authenticationViewModel.signOut()
        findNavController().navigate(R.id.action_signedInMockupFragment_to_welcomeFragment)
    }

    fun viewPublicEventsList() {
        findNavController().navigate(R.id.action_signedInMockupFragment_to_eventsListFragment)
    }

    fun viewCouncilMeetEventsList() {
        findNavController().navigate(R.id.action_signedInMockupFragment_to_councilMeetEventsListFragment)
    }

    fun goToReportIncident() {
        val action =
            SignedInMockupFragmentDirections.actionSignedInMockupFragmentToReportIncidentFragment(
                citizenId = args.connectedUserId
            )
        findNavController().navigate(action)
    }
}

fun Fragment.toastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastErrorMessage(errorMessage: String = DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN) {
    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
}