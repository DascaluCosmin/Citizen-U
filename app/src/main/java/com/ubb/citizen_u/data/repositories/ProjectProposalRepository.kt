package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface ProjectProposalRepository {

    suspend fun proposeProject(
        projectProposal: ProjectProposal,
        listProposedProjectAttachment: List<Attachment>,
    ): Flow<Response<Boolean>>
}