package com.ubb.citizen_u.domain.usescases.projectproposals

import com.ubb.citizen_u.data.repositories.ProjectProposalRepository
import javax.inject.Inject

class GetCitizenProposedProjectsUseCase @Inject constructor(
    private val projectProposalRepository: ProjectProposalRepository,
) {
    suspend operator fun invoke(citizenId: String) =
        projectProposalRepository.getAllProposedProjects(citizenId)
}
