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
import com.ubb.citizen_u.databinding.FragmentReportedIncidentsListBinding
import com.ubb.citizen_u.ui.viewmodels.CitizenRequestViewModel
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReportedIncidentsListFragment : Fragment() {

    companion object {
        private const val TAG = "ReportedIncidentsListFragment"
    }

    private var _binding: FragmentReportedIncidentsListBinding? = null
    private val binding: FragmentReportedIncidentsListBinding get() = _binding!!

    private val citizenViewModel: CitizenViewModel by activityViewModels()
    private val citizenRequestViewModel: CitizenRequestViewModel by activityViewModels()
    private val args: ReportedIncidentsListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportedIncidentsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetCitizenReportedIncidentsState() }
            }
        }
    }

    private suspend fun collectGetCitizenReportedIncidentsState() {
        citizenRequestViewModel.getCitizenReportedIncidentsState.collect {
            Log.d(TAG, "Collecting citizen reported incidents...")
        }
    }

    private suspend fun collectGetOthersReportedIncidentsState() {
        citizenRequestViewModel.getOthersReportedIncidentsState.collect {
            Log.d(TAG, "Collecting other citizens reported incidents...")
        }
    }

    override fun onStart() {
        super.onStart()
        val currentCitizenId = citizenViewModel.citizenId
        if (args.forCurrentCitizen) {
            citizenRequestViewModel.getCitizenReportedIncidents(currentCitizenId)
        } else {
            citizenRequestViewModel.getOthersReportedIncidents(currentCitizenId)
        }
    }
}