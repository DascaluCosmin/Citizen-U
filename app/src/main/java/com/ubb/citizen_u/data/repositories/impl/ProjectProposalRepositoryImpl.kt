package com.ubb.citizen_u.data.repositories.impl

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.ubb.citizen_u.data.Details
import com.ubb.citizen_u.data.model.Attachment
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposal
import com.ubb.citizen_u.data.model.citizens.proposals.ProjectProposalData
import com.ubb.citizen_u.data.repositories.*
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.CitizenConstants
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import com.ubb.citizen_u.util.DatabaseConstants
import com.ubb.citizen_u.util.DatabaseConstants.COMMENTS_COL
import com.ubb.citizen_u.util.DatabaseConstants.PROPOSED_PROJECTS_COL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProjectProposalRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
    private val attachmentRepository: AttachmentRepository,
    private val citizenRepository: CitizenRepository,
    private val photoRepository: PhotoRepository,
    private val commentRepository: CommentRepository,
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
                Thread.sleep(2000L)

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

    override suspend fun getProjectProposal(
        citizenId: String,
        projectProposalId: String,
    ): Flow<Response<ProjectProposalData?>> =
        flow {
            try {
                emit(Response.Loading)

                val projectProposalSnapshot = usersRef.document(citizenId)
                    .collection(PROPOSED_PROJECTS_COL).document(projectProposalId).get()
                    .await()

                val proposedProject = getProposedProject(
                    projectProposalDocSnapshot = projectProposalSnapshot,
                    citizenId = citizenId,
                    listDetails = listOf(Details.PHOTOS, Details.COMMENTS, Details.DOCUMENTS)
                )

                emit(Response.Success(proposedProject))
            } catch (exception: Exception) {
                Log.e(TAG, "getProposedProject: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllProposedProjects(citizenId: String): Flow<Response<List<ProjectProposalData?>>> =
        flow {
            try {
                emit(Response.Loading)

                val projectProposals = getAllProjectProposals(
                    citizenId = citizenId,
                    listDetails = listOf(Details.PHOTOS)
                )

                emit(Response.Success(projectProposals))
            } catch (exception: Exception) {
                Log.e(TAG, "getAllProposedProject: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllProposedProjectsOfOthers(currentCitizenId: String): Flow<Response<List<ProjectProposalData?>>> =
        flow {
            try {
                emit(Response.Loading)

                val proposedProjects = getAllProposedProjectsOfOthersList(currentCitizenId)
                emit(Response.Success(proposedProjects))
            } catch (exception: Exception) {
                Log.e(TAG,
                    "getAllProposedProjectsOfOthers: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun addCommentToProjectProposal(
        projectProposal: ProjectProposal,
        comment: Comment,
    ): Flow<Response<Boolean>> =
        flow {
            try {
                emit(Response.Loading)
                if (projectProposal.proposedBy == null) {
                    emit(Response.Error(DEFAULT_ERROR_MESSAGE))
                    return@flow
                }

                usersRef.document(projectProposal.proposedBy!!.id)
                    .collection(PROPOSED_PROJECTS_COL)
                    .document(projectProposal.id)
                    .collection(COMMENTS_COL)
                    .add(comment)
                    .await()

                emit(Response.Success(true))
            } catch (exception: Exception) {
                Log.e(TAG,
                    "addCommentToProjectProposal: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    private suspend fun getAllProjectProposals(
        citizenId: String,
        listDetails: List<Details>,
    ): List<ProjectProposalData?> {
        val projectSnapshot =
            usersRef.document(citizenId).collection(PROPOSED_PROJECTS_COL).get().await()
        return projectSnapshot.documents.map {
            getProposedProject(
                projectProposalDocSnapshot = it,
                citizenId = citizenId,
                listDetails = listDetails
            )
        }
    }

    private suspend fun getAllProposedProjectsOfOthersList(currentCitizenId: String): List<ProjectProposalData?> {
        val usersSnapshot = usersRef.get().await()
        return usersSnapshot.documents
            .filterNot { it.id == currentCitizenId }
            .map {
                getAllProjectProposals(
                    citizenId = it.id,
                    listDetails = listOf(Details.PHOTOS)
                )
            }.flatten()
    }

    private suspend fun getProposedProject(
        projectProposalDocSnapshot: DocumentSnapshot,
        citizenId: String,
        listDetails: List<Details>,
    ): ProjectProposalData? {
        return projectProposalDocSnapshot.toObject(ProjectProposalData::class.java)?.apply {
            if (listDetails.contains(Details.PHOTOS)) {
                this.photos = photoRepository.getAllProposedProjectPhotos(citizenId, this.id)
            }

            if (listDetails.contains(Details.COMMENTS)) {
                comments = commentRepository.getAllComments(projectProposalDocSnapshot)
            }

            citizenRepository.getCitizen(citizenId).collect { citizenResponse ->
                if (citizenResponse is Response.Success) {
                    this.proposedBy = citizenResponse.data
                }
            }
        }
    }


}