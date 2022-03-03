package com.ubb.citizen_u.ui.fragments.reports.transitions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.databinding.FragmentCitizenReportedIncidentsTransitionBinding
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel

class CitizenReportedIncidentsTransitionFragment : Fragment() {

    private var _binding: FragmentCitizenReportedIncidentsTransitionBinding? = null
    private val binding: FragmentCitizenReportedIncidentsTransitionBinding get() = _binding!!

    private val citizenViewModel: CitizenViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            FragmentCitizenReportedIncidentsTransitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val action =
            CitizenReportedIncidentsTransitionFragmentDirections
                .actionCitizenReportedIncidentsTransitionFragmentToReportedIncidentsListFragment(
                    forCurrentCitizen = true
                )
        findNavController().navigate(action)
    }
}