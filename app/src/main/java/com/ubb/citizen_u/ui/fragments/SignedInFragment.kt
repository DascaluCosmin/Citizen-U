package com.ubb.citizen_u.ui.fragments

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
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
import com.ubb.citizen_u.ui.AuthenticationActivity
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.util.NotificationsConstants.CHANNEL_DESCRIPTION
import com.ubb.citizen_u.util.NotificationsConstants.CHANNEL_ID
import com.ubb.citizen_u.util.NotificationsConstants.CHANNEL_NAME
import com.ubb.citizen_u.util.NotificationsConstants.NOTIFICATION_ID
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
//        findNavController().navigate(R.id.action_signedInFragment_to_reportIncidentFragment)
        createNotificationsChannel()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationsChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val manager = requireContext()
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(requireContext()).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setContentTitle(getString(R.string.city_hall_name))
            .setContentText("Test Text")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.logo_bg_free)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
