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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentReportedIncidentsListBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.adapters.ReportedIncidentsAdapter
import com.ubb.citizen_u.ui.util.toastErrorMessage
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

    private lateinit var adapter: ReportedIncidentsAdapter

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

        adapter = ReportedIncidentsAdapter {
            if (it.citizen == null) {
                toastErrorMessage()
                return@ReportedIncidentsAdapter
            }

            it.citizen?.run {
                val action = ReportedIncidentsListFragmentDirections
                    .actionReportedIncidentsListFragmentToReportedIncidentDetailsFragment(
                        incidentId = it.id,
                        citizenId = id
                    )
                findNavController().navigate(action)
            }
        }

        binding.apply {
            reportedIncidentsFragment = this@ReportedIncidentsListFragment
            reportedIncidentsRecyclerview.adapter = adapter

            if (isCurrentCitizen()) {
                reportedIncidentsTextview.text =
                    getString(R.string.citizen_reported_incidents_list_fragment_label)
            } else {
                reportedIncidentsTextview.text =
                    getString(R.string.other_citizens_reported_incidents_list_fragment_label)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetCitizenReportedIncidentsState() }
                launch { collectGetOthersReportedIncidentsState() }
            }
        }
    }

    private suspend fun collectGetCitizenReportedIncidentsState() {
        citizenRequestViewModel.getCitizenReportedIncidentsState.collect {
            Log.d(TAG, "Collecting citizen reported incidents...")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.e(
                        TAG,
                        "collectGetCitizenReportedIncidentsState: An error has occurred: ${it.message}",
                    )
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    Log.d(TAG,
                        "collectGetCitizenReportedIncidentsState: Successfully collected ${it.data.size} reported incidents")
                    binding.mainProgressbar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        }
    }

    private suspend fun collectGetOthersReportedIncidentsState() {
        citizenRequestViewModel.getOthersReportedIncidentsState.collect {
            Log.d(TAG, "Collecting other citizens reported incidents...")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.e(
                        TAG,
                        "collectGetOthersReportedIncidentsState: An error has occurred: ${it.message}"
                    )
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    Log.d(TAG,
                        "collectGetOthersReportedIncidentsState: Successfully collected ${it.data.size} reported incidents by other citizens")
                    binding.mainProgressbar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentCitizenId = citizenViewModel.citizenId
        if (isCurrentCitizen()) {
            citizenRequestViewModel.getCitizenReportedIncidents(currentCitizenId)
        } else {
            citizenRequestViewModel.getOthersReportedIncidents(currentCitizenId)
        }
    }

    private fun isCurrentCitizen(): Boolean {
        return args.forCurrentCitizen
    }
}