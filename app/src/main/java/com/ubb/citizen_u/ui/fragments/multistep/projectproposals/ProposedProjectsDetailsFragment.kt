package com.ubb.citizen_u.ui.fragments.multistep.projectproposals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.databinding.FragmentProposedProjectsDetailsBinding

class ProposedProjectsDetailsFragment : Fragment() {

    companion object {
        private const val TAG = "UBB-ProposedProjectsDetailsFragment"
    }

    private var _binding: FragmentProposedProjectsDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProposedProjectsDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


}