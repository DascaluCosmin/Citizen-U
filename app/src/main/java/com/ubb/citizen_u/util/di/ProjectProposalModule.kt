package com.ubb.citizen_u.util.di

import com.ubb.citizen_u.data.repositories.ProjectProposalRepository
import com.ubb.citizen_u.data.repositories.impl.ProjectProposalRepositoryImpl
import com.ubb.citizen_u.domain.usescases.projectproposals.ProjectProposalUseCases
import com.ubb.citizen_u.domain.usescases.projectproposals.ProposeProjectUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProjectProposalModule {

    @Provides
    @Singleton
    fun providesProjectProposalRepository(): ProjectProposalRepository =
        ProjectProposalRepositoryImpl()

    @Provides
    @Singleton
    fun providesProjectProposalUseCases(projectProposalRepository: ProjectProposalRepository): ProjectProposalUseCases =
        ProjectProposalUseCases(
            proposeProjectUseCase = ProposeProjectUseCase(projectProposalRepository)
        )
}