package com.ubb.citizen_u.ui.fragments.reports

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
import com.ubb.citizen_u.databinding.FragmentReportedIncidentDetailsBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReportedIncidentDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ReportedIncidentDetailsFragment"
    }

    private var _binding: FragmentReportedIncidentDetailsBinding? = null
    private val binding get() = _binding!!

    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()
    private val args: ReportedIncidentDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportedIncidentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            reportedIncidentDetailsFragment = this@ReportedIncidentDetailsFragment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetReportedIncidentState() }
            }
        }
    }

    private suspend fun collectGetReportedIncidentState() {
        citizenRequestViewModel.getReportedIncidentState.collect {
            Log.d(TAG, "collectGetReportedIncidentState: Collecting response $it")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                    binding.reportedIncidentImage.visibility = View.GONE
                    binding.reportedIncidentDetails.visibility = View.GONE
                }
                is Response.Error -> {
                    binding.mainProgressbar.visibility = View.GONE
                    binding.reportedIncidentImage.visibility = View.GONE
                    binding.reportedIncidentDetails.visibility = View.GONE
                }
                is Response.Success -> {
                    binding.mainProgressbar.visibility = View.GONE
                    binding.reportedIncidentImage.visibility = View.VISIBLE
                    binding.reportedIncidentDetails.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        citizenRequestViewModel.getReportedIncident(
            citizenId = args.citizenId,
            incidentId = args.incidentId
        )
    }
}