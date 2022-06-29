package com.ubb.citizen_u.domain.usescases.projectproposals

import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposalData
import com.ubb.citizen_u.data.repositories.ProjectProposalRepository
import javax.inject.Inject

class VoteProjectProposalUseCase @Inject constructor(
    private val projectProposalRepository: ProjectProposalRepository,
) {
    suspend operator fun invoke(projectProposal: ProjectProposalData, citizenId: String) =
        projectProposalRepository.voteProject(projectProposal, citizenId)
}
