package com.ubb.citizen_u.ui.fragments.multistep.projectproposals.transitions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ubb.citizen_u.databinding.FragmentOtherCitizensProposedProjectsTransitionBinding

class OtherCitizensProposedProjectsTransitionFragment : Fragment() {

    companion object {
        const val TAG = "UBB-OtherCitizensProposedProjectsTransitionFragment"
    }

    private var _binding: FragmentOtherCitizensProposedProjectsTransitionBinding? = null
    private val binding: FragmentOtherCitizensProposedProjectsTransitionBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOtherCitizensProposedProjectsTransitionBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val action = OtherCitizensProposedProjectsTransitionFragmentDirections
            .actionOtherCitizensProposedProjectsTransitionFragmentToProposedProjectsListFragment(
                forCurrentCitizen = false
            )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}