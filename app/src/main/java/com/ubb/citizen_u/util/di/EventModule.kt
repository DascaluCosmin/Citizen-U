package com.ubb.citizen_u.util.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ubb.citizen_u.data.repositories.EventRepository
import com.ubb.citizen_u.data.repositories.PhotoRepository
import com.ubb.citizen_u.data.repositories.impl.EventRepositoryImpl
import com.ubb.citizen_u.data.repositories.impl.PhotoRepositoryImpl
import com.ubb.citizen_u.domain.usescases.events.*
import com.ubb.citizen_u.util.DatabaseConstants.PUBLIC_EVENTS_COL
import com.ubb.citizen_u.util.DatabaseConstants.PUBLIC_RELEASE_EVENTS_COL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventModule {

    @Provides
    @Singleton
    fun providesEventRepository(
        @Named(PUBLIC_EVENTS_COL) publicEventsRef: CollectionReference,
        @Named(PUBLIC_RELEASE_EVENTS_COL) publicReleaseEventsRef: CollectionReference,
        photoRepository: PhotoRepository,
    ): EventRepository =
        EventRepositoryImpl(
            publicEventsRef = publicEventsRef,
            publicReleaseEventsRef = publicReleaseEventsRef,
            photoRepository = photoRepository
        )

    @Provides
    @Singleton
    fun providesEventPhotoRepository(firebaseStorage: FirebaseStorage): PhotoRepository =
        PhotoRepositoryImpl(
            firebaseStorage = firebaseStorage
        )

    @Provides
    @Singleton
    fun providesEventUseCases(eventRepository: EventRepository): EventUseCases =
        EventUseCases(
            getAllPublicEventsUseCase = GetAllPublicEventsUseCase(eventRepository),
            getAllPublicEventsOrderedByDateUseCase = GetAllPublicEventsOrderedByDateUseCase(
                eventRepository
            ),
            getPublicEventDetailsUseCase = GetPublicEventDetailsUseCase(eventRepository),
            getAllPublicReleaseEvents = GetAllPublicReleaseEvents(eventRepository),
            getAllPublicReleaseEventsOrderedByUseCase = GetAllPublicReleaseEventsOrderedByDateUseCase(
                eventRepository
            ),
            getAllPeriodicEventsUseCase = GetAllPeriodicEventsUseCase(eventRepository),
            getPublicReleaseDetailsUseCase = GetPublicReleaseEventDetailsUseCase(eventRepository)
        )

    @Provides
    @Singleton
    @Named(PUBLIC_EVENTS_COL)
    fun providesPublicEventsRef(firebaseFirestore: FirebaseFirestore) =
        firebaseFirestore.collection(PUBLIC_EVENTS_COL)

    @Provides
    @Singleton
    @Named(PUBLIC_RELEASE_EVENTS_COL)
    fun providesPublicReleaseEventsRef(firebaseFirestore: FirebaseFirestore): CollectionReference =
        firebaseFirestore.collection(PUBLIC_RELEASE_EVENTS_COL)
}