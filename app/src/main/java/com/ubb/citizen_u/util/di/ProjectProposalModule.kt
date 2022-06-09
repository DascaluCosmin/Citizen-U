package com.ubb.citizen_u.util.di

import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.repositories.*
import com.ubb.citizen_u.data.repositories.impl.ProjectProposalRepositoryImpl
import com.ubb.citizen_u.domain.usescases.projectproposals.*
import com.ubb.citizen_u.util.DatabaseConstants.USERS_COL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProjectProposalModule {

    @Provides
    @Singleton
    fun providesProjectProposalRepository(
        @Named(USERS_COL) usersRef: CollectionReference,
        attachmentRepository: AttachmentRepository,
        citizenRepository: CitizenRepository,
        photoRepository: PhotoRepository,
        commentRepository: CommentRepository,
        documentRepository: DocumentRepository,
    ): ProjectProposalRepository =
        ProjectProposalRepositoryImpl(
            usersRef = usersRef,
            attachmentRepository = attachmentRepository,
            citizenRepository = citizenRepository,
            photoRepository = photoRepository,
            commentRepository = commentRepository,
            documentRepository = documentRepository
        )

    @Provides
    @Singleton
    fun providesProjectProposalUseCases(projectProposalRepository: ProjectProposalRepository): ProjectProposalUseCases =
        ProjectProposalUseCases(
            proposeProjectUseCase = ProposeProjectUseCase(projectProposalRepository),
            getCitizenProposedProjectsUseCase = GetCitizenProposedProjectsUseCase(
                projectProposalRepository),
            getOthersProposedProjectsUseCase = GetOthersProposedProjectsUseCase(
                projectProposalRepository),
            getProposedProjectUseCase = GetProposedProjectUseCase(projectProposalRepository),
            addCommentToProjectProposalUseCase = AddCommentToProjectProposalUseCase(
                projectProposalRepository),
            voteProjectProposalUseCaseUseCase = VoteProjectProposalUseCase(projectProposalRepository),
            undoVoteProjectProposalUseCaseUseCase = UndoVoteProjectProposalUseCase(
                projectProposalRepository)
        )
}