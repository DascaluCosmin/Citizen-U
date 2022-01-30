package com.ubb.citizen_u.util.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ubb.citizen_u.data.repositories.EventPhotoRepository
import com.ubb.citizen_u.data.repositories.EventRepository
import com.ubb.citizen_u.data.repositories.impl.EventPhotoRepositoryImpl
import com.ubb.citizen_u.data.repositories.impl.EventRepositoryImpl
import com.ubb.citizen_u.domain.usescases.event.*
import com.ubb.citizen_u.util.DatabaseConstants.COUNCIL_MEET_EVENTS_COL
import com.ubb.citizen_u.util.DatabaseConstants.PUBLIC_EVENTS_COL
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
        @Named(COUNCIL_MEET_EVENTS_COL) councilEventsRef: CollectionReference,
        eventPhotoRepository: EventPhotoRepository
    ): EventRepository =
        EventRepositoryImpl(
            publicEventsRef = publicEventsRef,
            councilMeetEventsRef = councilEventsRef,
            eventPhotoRepository = eventPhotoRepository
        )

    @Provides
    @Singleton
    fun providesEventPhotoRepository(firebaseStorage: FirebaseStorage): EventPhotoRepository =
        EventPhotoRepositoryImpl(
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
            getAllCouncilMeetEventsUseCase = GetAllCouncilMeetEventsUseCase(eventRepository),
            getAllCouncilMeetEventsOrderedByUseCase = GetAllCouncilMeetEventsOrderedByDateUseCase(
                eventRepository
            )
        )

    @Provides
    @Singleton
    @Named(PUBLIC_EVENTS_COL)
    fun providesPublicEventsRef(firebaseFirestore: FirebaseFirestore) =
        firebaseFirestore.collection(PUBLIC_EVENTS_COL)

    @Provides
    @Singleton
    @Named(COUNCIL_MEET_EVENTS_COL)
    fun providesCouncilMeetEventsRef(firebaseFirestore: FirebaseFirestore) =
        firebaseFirestore.collection(COUNCIL_MEET_EVENTS_COL)
}