package com.ubb.citizen_u.di

import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.repositories.CitizenRepository
import com.ubb.citizen_u.data.repositories.impl.CitizenRepositoryImpl
import com.ubb.citizen_u.domain.usescases.citizen.CitizenUseCases
import com.ubb.citizen_u.domain.usescases.citizen.GetCitizenUseCase
import com.ubb.citizen_u.util.DatabaseConstants.USERS_COL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CitizenModule {

    @Provides
    @Singleton
    fun providesCitizenRepository(
        @Named(USERS_COL) usersRef: CollectionReference
    ): CitizenRepository =
        CitizenRepositoryImpl(
            usersRef = usersRef
        )

    @Provides
    @Singleton
    fun providesCitizenUseCases(citizenRepository: CitizenRepository) =
        CitizenUseCases(
            getCitizenUseCase = GetCitizenUseCase(citizenRepository)
        )
}