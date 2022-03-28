package com.ubb.citizen_u.ui.fragments.multistep.projectproposals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.databinding.FragmentProposedProjectsListBinding

class ProposedProjectsListFragment : Fragment() {

    companion object {
        const val TAG = "UBB-ProposedProjectsListFragment"
    }

    private var _binding: FragmentProposedProjectsListBinding? = null
    private val binding: FragmentProposedProjectsListBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProposedProjectsListBinding.inflate(inflater, container, false)
        return binding.root
    }
}