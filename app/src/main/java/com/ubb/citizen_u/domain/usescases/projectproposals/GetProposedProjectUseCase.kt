package com.ubb.citizen_u.domain.usescases.projectproposals

import com.ubb.citizen_u.data.repositories.ProjectProposalRepository
import javax.inject.Inject

class GetProposedProjectUseCase @Inject constructor(
    private val projectProposalRepository: ProjectProposalRepository,
) {

    suspend operator fun invoke(citizenId: String, projectProposalId: String) =
        projectProposalRepository.getProjectProposal(
            citizenId = citizenId,
            projectProposalId = projectProposalId
        )
}
