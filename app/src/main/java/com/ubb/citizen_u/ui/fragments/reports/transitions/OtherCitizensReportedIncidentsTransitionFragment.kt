package com.ubb.citizen_u.ui.fragments.reports.transitions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.databinding.FragmentOtherCitizensReportedIncidentsTransitionBinding

class OtherCitizensReportedIncidentsTransitionFragment : Fragment() {

    private var _binding: FragmentOtherCitizensReportedIncidentsTransitionBinding? = null
    private val binding: FragmentOtherCitizensReportedIncidentsTransitionBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOtherCitizensReportedIncidentsTransitionBinding.inflate(inflater,
            container,
            false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val action =
            OtherCitizensReportedIncidentsTransitionFragmentDirections
                .actionOtherCitizensReportedIncidentsTransitionFragmentToReportedIncidentsListFragment(
                    forCurrentCitizen = false
                )
        findNavController().navigate(action)
    }
}