package com.ubb.citizen_u.ui.fragments.multistep.projectproposals

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ubb.citizen_u.R
import com.ubb.citizen_u.databinding.FragmentProposedProjectsListBinding
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.ui.adapters.ProposedProjectsAdapter
import com.ubb.citizen_u.ui.util.toastErrorMessage
import com.ubb.citizen_u.ui.viewmodels.CitizenViewModel
import com.ubb.citizen_u.ui.viewmodels.ProjectProposalViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProposedProjectsListFragment : Fragment() {

    companion object {
        const val TAG = "UBB-ProposedProjectsListFragment"
    }

    private var _binding: FragmentProposedProjectsListBinding? = null
    private val binding: FragmentProposedProjectsListBinding get() = _binding!!

    private lateinit var adapter: ProposedProjectsAdapter

    private val citizenViewModel: CitizenViewModel by activityViewModels()
    private val projectProposalViewModel: ProjectProposalViewModel by activityViewModels()
    private val args: ProposedProjectsListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProposedProjectsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProposedProjectsAdapter {
            if (it.proposedBy == null) {
                toastErrorMessage()
                return@ProposedProjectsAdapter
            }

            it.proposedBy?.run {
                val action = ProposedProjectsListFragmentDirections
                    .actionProposedProjectsListFragmentToProposedProjectsDetailsFragment(
                        projectProposalId = it.id,
                        citizenId = id
                    )

                findNavController().navigate(action)
            }
        }

        binding.apply {
            proposedProjectsFragment = this@ProposedProjectsListFragment
            proposedProjectsRecyclerview.adapter = adapter

            if (isCurrentCitizen()) {
                proposedProjectsTextview.text =
                    getString(R.string.citizen_proposed_projects_list_fragment_label)
            } else {
                proposedProjectsTextview.text =
                    getString(R.string.others_citizens_proposed_projects_list_fragment_label)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectGetCitizenProposedProjectsState() }
                launch { collectGetOtherCitizensProposedProjectsState() }
            }
        }
    }

    private suspend fun collectGetCitizenProposedProjectsState() {
        projectProposalViewModel.getCitizenProposedProjectsState.collect {
            Log.d(TAG, "Collecting citizen proposed projects...")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.e(TAG, "An error has occurred: ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    Log.d(TAG, "Successfully collected ${it.data.size} proposed projects")
                    binding.mainProgressbar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        }
    }

    private suspend fun collectGetOtherCitizensProposedProjectsState() {
        projectProposalViewModel.getOthersProposedProjectsState.collect {
            Log.d(TAG, "Collection other citizens proposed projects...")
            when (it) {
                Response.Loading -> {
                    binding.mainProgressbar.visibility = View.VISIBLE
                }
                is Response.Error -> {
                    Log.e(TAG, "An error has occurred: ${it.message}")
                    binding.mainProgressbar.visibility = View.GONE
                    toastErrorMessage()
                }
                is Response.Success -> {
                    Log.d(TAG, "Successfully collected ${it.data.size} proposed projects")
                    binding.mainProgressbar.visibility = View.GONE
                    adapter.submitList(it.data)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentCitizenId = citizenViewModel.citizenId
        if (isCurrentCitizen()) {
            projectProposalViewModel.getCitizenProposedProjects(currentCitizenId)
        } else {
            projectProposalViewModel.getOtherCitizensProposedProjects(currentCitizenId)
        }
    }

    private fun isCurrentCitizen(): Boolean {
        return args.forCurrentCitizen
    }
}