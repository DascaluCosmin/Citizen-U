package com.ubb.citizen_u.data.repositories.impl

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.data.repositories.AttachmentRepository
import com.ubb.citizen_u.data.repositories.ProjectProposalRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.CitizenConstants
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import com.ubb.citizen_u.util.DatabaseConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProjectProposalRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
    private val attachmentRepository: AttachmentRepository,
) : ProjectProposalRepository {

    companion object {
        private const val TAG = "UBB-ProjectProposalRepositoryImpl"
    }

    override suspend fun proposeProject(
        projectProposal: ProjectProposal,
        listProposedProjectAttachment: List<Attachment>,
    ): Flow<Response<Boolean>> =
        flow {
            try {
                emit(Response.Loading)
                val proposedById = projectProposal.proposedBy?.id
                if (proposedById.isNullOrEmpty()) {
                    emit(Response.Error(CitizenConstants.CITIZEN_MISSING_ERROR_MESSAGE))
                    return@flow
                }

                val result = usersRef.document(proposedById)
                    .collection(DatabaseConstants.PROPOSED_PROJECTS_COL)
                    .add(projectProposal.mapToModelClass())
                    .await()

                listProposedProjectAttachment.forEach { attachment ->
                    attachmentRepository.saveAttachment(
                        attachment = attachment,
                        citizenId = projectProposal.proposedBy!!.id,
                        projectProposalId = result.id)
                }
                emit(Response.Success(true))
            } catch (exception: Exception) {
                Log.e(TAG, "An error has occurred at proposing a new project: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }
}