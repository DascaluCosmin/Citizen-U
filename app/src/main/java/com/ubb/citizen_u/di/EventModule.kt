package com.ubb.citizen_u.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ubb.citizen_u.data.repositories.EventRepository
import com.ubb.citizen_u.data.repositories.impl.EventRepositoryImpl
import com.ubb.citizen_u.domain.usescases.event.EventUseCases
import com.ubb.citizen_u.domain.usescases.event.GetAllEventsUseCase
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
    fun providesEventRepository(@Named(EVENTS_COL) eventsRef: CollectionReference): EventRepository =
        EventRepositoryImpl(
            eventsRef = eventsRef
        )

    @Provides
    @Singleton
    fun providesEventUseCases(eventRepository: EventRepository): EventUseCases =
        EventUseCases(
            getAllEventsUseCase = GetAllEventsUseCase(eventRepository)
        )

    @Provides
    @Singleton
    @Named(EVENTS_COL)
    fun providesEventsRef(firebaseFirestore: FirebaseFirestore) =
        firebaseFirestore.collection(EVENTS_COL)
}