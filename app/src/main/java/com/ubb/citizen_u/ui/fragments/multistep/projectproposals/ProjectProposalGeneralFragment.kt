package com.ubb.citizen_u.ui.fragments.multistep.projectproposals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.databinding.FragmentProjectProposalGeneralBinding

class ProjectProposalGeneralFragment : Fragment() {

    companion object {
        const val TAG = "UBB-ProjectProposalGeneralFragment"
    }

    private var _binding: FragmentProjectProposalGeneralBinding? = null
    private val binding: FragmentProjectProposalGeneralBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProjectProposalGeneralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            fragmentProjectProposalGeneral = this@ProjectProposalGeneralFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}