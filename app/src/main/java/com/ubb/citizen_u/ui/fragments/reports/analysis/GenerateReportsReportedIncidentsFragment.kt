package com.ubb.citizen_u.ui.fragments.reports.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.R

class GenerateReportsReportedIncidentsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-GenerateReportsReportedIncidentsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generate_reports_reported_incidents,
            container,
            false)
    }
}