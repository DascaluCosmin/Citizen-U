package com.ubb.citizen_u.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.databinding.ProposedProjectListItemBinding
import com.ubb.citizen_u.ui.adapters.viewholders.ProposedProjectsViewHolder

class ProposedProjectsAdapter(
    private val proposedProjectDetailsOnClickCallBack: (ProjectProposal) -> Unit,
) : ListAdapter<ProjectProposal, RecyclerView.ViewHolder>(ProjectProposalDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProposedProjectsViewHolder(
            proposedProjectListItemBinding = ProposedProjectListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            proposedProjectDetailsOnClickCallBack = proposedProjectDetailsOnClickCallBack
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val projectProposal = getItem(position)
        (holder as ProposedProjectsViewHolder).bind(projectProposal)
    }

    class ProjectProposalDiffCallBack : DiffUtil.ItemCallback<ProjectProposal>() {
        override fun areItemsTheSame(oldItem: ProjectProposal, newItem: ProjectProposal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ProjectProposal,
            newItem: ProjectProposal,
        ): Boolean {
            return oldItem == newItem
        }
    }
}