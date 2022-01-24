package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.model.events.Event
import com.ubb.citizen_u.data.repositories.EventRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventsRef: CollectionReference
) : EventRepository {

    override suspend fun getAllEvents(): Flow<Response<List<Event?>>> =
        flow {
            try {
                emit(Response.Loading)

                val eventsSnapshot = eventsRef.get().await()
                val events = eventsSnapshot.documents.map {
                    it.toObject(Event::class.java)?.apply {
                        id = it.id
                    }
                }

                emit(Response.Success(events))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }
}