package com.ubb.citizen_u.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposalData
import com.ubb.citizen_u.databinding.ProposedProjectListItemBinding
import com.ubb.citizen_u.util.DateFormatter
import com.ubb.citizen_u.util.glide.ImageFiller

class ProposedProjectsViewHolder(
    private val proposedProjectListItemBinding: ProposedProjectListItemBinding,
    private val proposedProjectDetailsOnClickCallBack: (ProjectProposal) -> Unit,
) : RecyclerView.ViewHolder(proposedProjectListItemBinding.root) {

    fun bind(projectProposal: ProjectProposal) {
        proposedProjectListItemBinding.apply {
            proposedProjectTitle.text = projectProposal.title
            proposedProjectLocation.text = projectProposal.location
            proposedProjectCategory.text = projectProposal.category
            proposedProjectVotes.text = projectProposal.numberOfVotes.toString()

            projectProposal.proposedOn?.let {
                proposedProjectProposedDate.text = DateFormatter.format(it)
            }

            if (projectProposal is ProjectProposalData) {
                projectProposal.photos.let { photos ->
                    if (photos.isNotEmpty()) {
                        photos[0]?.let { photo ->
                            ImageFiller.fill(itemView.context, proposedProjectImage, photo)
                        }
                    }
                }
            }

            proposedProjectCard.setOnClickListener {
                proposedProjectDetailsOnClickCallBack(projectProposal)
            }
        }
    }
}