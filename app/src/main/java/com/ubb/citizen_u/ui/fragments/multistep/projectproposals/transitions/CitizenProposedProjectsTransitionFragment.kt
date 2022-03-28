package com.ubb.citizen_u.ui.fragments.multistep.projectproposals.transitions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.databinding.FragmentCitizenProposedProjectsTransitionBinding

class CitizenProposedProjectsTransitionFragment : Fragment() {

    companion object {
        const val TAG = "UBB-CitizenProposedProjectsTransitionFragment"
    }

    private var _binding: FragmentCitizenProposedProjectsTransitionBinding? = null
    private val binding: FragmentCitizenProposedProjectsTransitionBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            FragmentCitizenProposedProjectsTransitionBinding.inflate(inflater, container, false)
        return binding.root
    }
}