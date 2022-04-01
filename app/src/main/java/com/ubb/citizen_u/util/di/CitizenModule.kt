package com.ubb.citizen_u.util.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ubb.citizen_u.data.repositories.AttachmentRepository
import com.ubb.citizen_u.data.repositories.CitizenRepository
import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import com.ubb.citizen_u.data.repositories.PhotoRepository
import com.ubb.citizen_u.data.repositories.impl.AttachmentRepositoryImpl
import com.ubb.citizen_u.data.repositories.impl.CitizenRepositoryImpl
import com.ubb.citizen_u.data.repositories.impl.CitizenRequestRepositoryImpl
import com.ubb.citizen_u.domain.usescases.citizens.CitizenUseCases
import com.ubb.citizen_u.domain.usescases.citizens.GetCitizenUseCase
import com.ubb.citizen_u.domain.usescases.citizens.requests.*
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
        @Named(USERS_COL) usersRef: CollectionReference,
    ): CitizenRepository =
        CitizenRepositoryImpl(
            usersRef = usersRef
        )

    @Provides
    @Singleton
    fun providesCitizenRequestRepository(
        @Named(USERS_COL) usersRef: CollectionReference,
        photoRepository: PhotoRepository,
        citizenRepository: CitizenRepository,
    ): CitizenRequestRepository =
        CitizenRequestRepositoryImpl(
            usersRef = usersRef,
            photoRepository = photoRepository,
            citizenRepository = citizenRepository
        )

    @Provides
    @Singleton
    fun providesAttachmentRepository(
        @Named(USERS_COL) usersRef: CollectionReference,
        firebaseStorage: FirebaseStorage,
    ): AttachmentRepository =
        AttachmentRepositoryImpl(
            usersRef = usersRef,
            firebaseStorage = firebaseStorage
        )

    @Provides
    @Singleton
    fun providesCitizenUseCases(citizenRepository: CitizenRepository) =
        CitizenUseCases(
            getCitizenUseCase = GetCitizenUseCase(citizenRepository)
        )

    @Provides
    @Singleton
    fun providesCitizenRequestUseCase(citizenRequestRepository: CitizenRequestRepository) =
        CitizenRequestUseCase(
            reportIncidentUseCase = ReportIncidentUseCase(citizenRequestRepository),
            getCitizenReportedIncidents = GetCitizenReportedIncidents(citizenRequestRepository),
            getOthersReportedIncidents = GetOthersReportedIncidents(citizenRequestRepository),
            getReportedIncident = GetReportedIncident(citizenRequestRepository),
            addCommentToIncident = AddCommentToIncident(citizenRequestRepository),
        )

    @Provides
    @Singleton
    @Named(USERS_COL)
    fun providesUsersRef(firebaseFirestore: FirebaseFirestore) =
        firebaseFirestore.collection(USERS_COL)
}