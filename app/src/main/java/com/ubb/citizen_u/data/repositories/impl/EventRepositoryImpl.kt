package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.ubb.citizen_u.data.model.events.Event
import com.ubb.citizen_u.data.model.events.EventPhoto
import com.ubb.citizen_u.data.repositories.EventPhotoRepository
import com.ubb.citizen_u.data.repositories.EventRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import com.ubb.citizen_u.util.DatabaseConstants.EVENTS_PHOTOS_COL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventsRef: CollectionReference,
    private val eventPhotoRepository: EventPhotoRepository
) : EventRepository {

    override suspend fun getAllEvents(): Flow<Response<List<Event?>>> =
        flow {
            try {
                emit(Response.Loading)

                val eventsSnapshot = eventsRef.get().await()
                val events = eventsSnapshot.documents.map {
                    it.toObject(Event::class.java)?.apply {
                        photos = getEventPhotos(it).apply {
                            setFirstEventPhotoStorageReference(it, this)
                        }
                    }
                }

                emit(Response.Success(events))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    private suspend fun setFirstEventPhotoStorageReference(
        eventsDocSnapshot: DocumentSnapshot, eventPhotos: MutableList<EventPhoto?>
    ) {
        if (eventPhotos.size > 0) {
            val firstPhotoId = eventPhotos[0]?.id
            if (firstPhotoId != null) {
                eventPhotos[0]?.storageReference =
                    eventPhotoRepository.getMainEventPhotoStorageReference(
                        eventsDocSnapshot.id, firstPhotoId
                    )
            }
        }
    }

    private suspend fun getEventPhotos(eventDocSnapshot: DocumentSnapshot): MutableList<EventPhoto?> {
        val eventPhotosSnapshot =
            eventDocSnapshot.reference.collection(EVENTS_PHOTOS_COL).get().await()

        val eventPhotos = eventPhotosSnapshot.documents.map {
            it.toObject(EventPhoto::class.java)
        }.toMutableList()

        return eventPhotos
    }
}