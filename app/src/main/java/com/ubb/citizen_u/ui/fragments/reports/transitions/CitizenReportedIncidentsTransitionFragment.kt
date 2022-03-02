package com.ubb.citizen_u.ui.fragments.reports.transitions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentCitizenReportedIncidentsTransitionBinding

class CitizenReportedIncidentsTransitionFragment : Fragment() {

    private var _binding: FragmentCitizenReportedIncidentsTransitionBinding? = null
    private val binding: FragmentCitizenReportedIncidentsTransitionBinding = _binding!!

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
        findNavController().navigate(R.id.action_citizenReportedIncidentsTransitionFragment_to_reportedIncidentsListFragment)
    }
}