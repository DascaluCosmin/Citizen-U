package com.ubb.citizen_u.util.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ubb.citizen_u.data.repositories.EventPhotoRepository
import com.ubb.citizen_u.data.repositories.EventRepository
import com.ubb.citizen_u.data.repositories.impl.EventPhotoRepositoryImpl
import com.ubb.citizen_u.data.repositories.impl.EventRepositoryImpl
import com.ubb.citizen_u.domain.usescases.event.EventUseCases
import com.ubb.citizen_u.domain.usescases.event.GetAllPublicEventsOrderedByDateUseCase
import com.ubb.citizen_u.domain.usescases.event.GetAllPublicEventsUseCase
import com.ubb.citizen_u.domain.usescases.event.GetPublicEventDetailsUseCase
import com.ubb.citizen_u.util.DatabaseConstants.EVENTS_COL
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
        @Named(EVENTS_COL) eventsRef: CollectionReference,
        eventPhotoRepository: EventPhotoRepository
    ): EventRepository =
        EventRepositoryImpl(
            eventsRef = eventsRef,
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
            getAllPublicPublicEventsUseCase = GetAllPublicEventsUseCase(eventRepository),
            getAllPublicPublicEventsOrderedByDateUseCase = GetAllPublicEventsOrderedByDateUseCase(eventRepository),
            getPublicEventDetailsUseCase = GetPublicEventDetailsUseCase(eventRepository)
        )

    @Provides
    @Singleton
    @Named(EVENTS_COL)
    fun providesEventsRef(firebaseFirestore: FirebaseFirestore) =
        firebaseFirestore.collection(EVENTS_COL)
}