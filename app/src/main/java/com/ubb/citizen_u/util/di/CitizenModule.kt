package com.ubb.citizen_u.util.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ubb.citizen_u.data.api.AddressApi
import com.ubb.citizen_u.data.repositories.*
import com.ubb.citizen_u.data.repositories.impl.*
import com.ubb.citizen_u.domain.usescases.citizens.CitizenUseCases
import com.ubb.citizen_u.domain.usescases.citizens.GetCitizenUseCase
import com.ubb.citizen_u.domain.usescases.citizens.requests.*
import com.ubb.citizen_u.util.DatabaseConstants.INCIDENTS_CATEGORIES_COL
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
        @Named(INCIDENTS_CATEGORIES_COL) incidentCategoriesRef: CollectionReference,
        photoRepository: PhotoRepository,
        citizenRepository: CitizenRepository,
        commentRepository: CommentRepository,
        addressApi: AddressApi,
    ): CitizenRequestRepository =
        CitizenRequestRepositoryImpl(
            usersRef = usersRef,
            incidentCategoriesRef = incidentCategoriesRef,
            photoRepository = photoRepository,
            citizenRepository = citizenRepository,
            commentRepository = commentRepository,
            addressApi = addressApi
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
    fun providesCommentRepository(): CommentRepository = CommentRepositoryImpl()

    @Provides
    @Singleton
    fun providesDocumentRepository(firebaseStorage: FirebaseStorage): DocumentRepository =
        DocumentRepositoryImpl(firebaseStorage)

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
            getAllReportedIncidents = GetAllReportedIncidents(citizenRequestRepository),
            getAllIncidentCategories = GetAllIncidentCategories(citizenRequestRepository),
        )

    @Provides
    @Singleton
    @Named(USERS_COL)
    fun providesUsersRef(firebaseFirestore: FirebaseFirestore) =
        firebaseFirestore.collection(USERS_COL)

    @Provides
    @Singleton
    @Named(INCIDENTS_CATEGORIES_COL)
    fun providesIncidentCategoriesRef(firebaseFirestore: FirebaseFirestore) =
        firebaseFirestore.collection(INCIDENTS_CATEGORIES_COL)
}