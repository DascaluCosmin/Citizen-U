package com.ubb.citizen_u.domain.usescases.projectproposals

import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.data.repositories.ProjectProposalRepository
import javax.inject.Inject

class AddCommentToProjectProposalUseCase @Inject constructor(
    private val projectProposalRepository: ProjectProposalRepository,
) {

    suspend operator fun invoke(projectProposal: ProjectProposal, comment: Comment) =
        projectProposalRepository.addCommentToProjectProposal(
            projectProposal = projectProposal,
            comment = comment
        )
}
