package com.ubb.citizen_u.util.di

import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.repositories.AttachmentRepository
import com.ubb.citizen_u.data.repositories.ProjectProposalRepository
import com.ubb.citizen_u.data.repositories.impl.ProjectProposalRepositoryImpl
import com.ubb.citizen_u.domain.usescases.projectproposals.ProjectProposalUseCases
import com.ubb.citizen_u.domain.usescases.projectproposals.ProposeProjectUseCase
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
    ): ProjectProposalRepository =
        ProjectProposalRepositoryImpl(
            usersRef = usersRef,
            attachmentRepository = attachmentRepository,
        )

    @Provides
    @Singleton
    fun providesProjectProposalUseCases(projectProposalRepository: ProjectProposalRepository): ProjectProposalUseCases =
        ProjectProposalUseCases(
            proposeProjectUseCase = ProposeProjectUseCase(projectProposalRepository)
        )
}