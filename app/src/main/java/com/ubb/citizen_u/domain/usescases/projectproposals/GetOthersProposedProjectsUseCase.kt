package com.ubb.citizen_u.domain.usescases.projectproposals

import com.ubb.citizen_u.data.repositories.ProjectProposalRepository
import javax.inject.Inject

class GetOthersProposedProjectsUseCase @Inject constructor(
    private val projectProposalRepository: ProjectProposalRepository,
) {
    suspend operator fun invoke(currentCitizenId: String) =
        projectProposalRepository.getAllProposedProjectsOfOthers(currentCitizenId)
}
