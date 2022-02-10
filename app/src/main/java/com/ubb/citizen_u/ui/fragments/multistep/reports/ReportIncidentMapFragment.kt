package com.ubb.citizen_u.ui.fragments.multistep.reports

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
import com.ubb.citizen_u.databinding.FragmentReportIncidentMapBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.fragments.toastErrorMessage
import com.ubb.citizen_u.ui.fragments.toastMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.util.CitizenRequestConstants.SUCCESSFUL_REPORT_INCIDENT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ReportIncidentMapFragment : Fragment() {

    companion object {
        private const val TAG = "ReportIncidentMapFragment"
    }

    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()
    private val args: ReportIncidentMapFragmentArgs by navArgs()

    private var _binding: FragmentReportIncidentMapBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportIncidentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            reportIncidentMapFragment = this@ReportIncidentMapFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectReportIncidentState() }
            }
        }
    }

    private suspend fun collectReportIncidentState() {
        citizenRequestViewModel.addReportIncidentState.collect {
            Log.d(TAG, "collectReportIncidentState: Collecting response $it...")
            when (it) {
                is Response.Error -> {
                    Log.d(TAG, "collectReportIncidentState: An error has occurred ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage(it.message)
                }
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    toastMessage(SUCCESSFUL_REPORT_INCIDENT)
                    goToUserProfile()
                }
            }
        }
    }

    private fun goToUserProfile() {
        val action =
            ReportIncidentMapFragmentDirections.actionReportIncidentMapFragmentToSignedInMockupFragment(
                connectedUserId = args.citizenId
            )
        findNavController().navigate(action)
    }

    fun sendIncidentReport() {
        Log.d(TAG, "sendIncidentReport: Sending incident report...")
        citizenRequestViewModel.reportIncident(
            description = args.incidentDescription,
            citizenId = args.citizenId
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}