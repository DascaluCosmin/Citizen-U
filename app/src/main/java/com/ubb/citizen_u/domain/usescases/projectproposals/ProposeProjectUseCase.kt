package com.ubb.citizen_u.domain.usescases.projectproposals

import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.data.repositories.ProjectProposalRepository
import javax.inject.Inject

class ProposeProjectUseCase @Inject constructor(
    val projectProposalRepository: ProjectProposalRepository
) {
    suspend operator fun invoke(projectProposal: ProjectProposal) {

    }
}
