package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposalData
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface ProjectProposalRepository {

    suspend fun proposeProject(
        projectProposal: ProjectProposal,
        listProposedProjectAttachment: List<Attachment>,
    ): Flow<Response<Boolean>>

    suspend fun voteProject(
        projectProposal: ProjectProposalData,
        citizenId: String,
    ): Flow<Response<Boolean>>

    suspend fun undoVoteProject(
        projectProposal: ProjectProposalData,
        citizenId: String,
    ): Flow<Response<Boolean>>

    suspend fun getProjectProposal(
        citizenId: String,
        projectProposalId: String,
    ): Flow<Response<ProjectProposalData?>>

    suspend fun getAllProposedProjects(citizenId: String): Flow<Response<List<ProjectProposalData?>>>

    suspend fun getAllProposedProjectsOfOthers(currentCitizenId: String): Flow<Response<List<ProjectProposalData?>>>

    suspend fun addCommentToProjectProposal(
        projectProposal: ProjectProposal,
        comment: Comment,
    ): Flow<Response<Boolean>>
}