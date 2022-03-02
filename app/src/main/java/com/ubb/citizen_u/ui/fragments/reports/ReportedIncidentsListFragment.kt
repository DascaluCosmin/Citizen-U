package com.ubb.citizen_u.ui.fragments.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.databinding.FragmentReportedIncidentsListBinding

class ReportedIncidentsListFragment : Fragment() {

    private var _binding: FragmentReportedIncidentsListBinding? = null
    private val binding: FragmentReportedIncidentsListBinding = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportedIncidentsListBinding.inflate(inflater, container, false)
        return binding.root
    }
}