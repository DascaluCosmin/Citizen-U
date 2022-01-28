package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.ubb.citizen_u.data.model.events.Event
import com.ubb.citizen_u.data.model.events.PublicEvent
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

    override suspend fun getAllPublicEvents(): Flow<Response<List<PublicEvent?>>> =
        flow {
            try {
                emit(Response.Loading)

                val events = getAllEventsList()
                emit(Response.Success(events))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllEventsOrderedByDate(): Flow<Response<List<PublicEvent?>>> =
        flow {
            try {
                emit(Response.Loading)

                val sortedEvents = getAllEventsList().sortedBy {
                    it?.startDate
                }
                emit(Response.Success(sortedEvents))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getPublicEventDetails(eventId: String): Flow<Response<PublicEvent?>> =
        flow {
            try {
                emit(Response.Loading)

                val eventSnapshot = eventsRef.document(eventId).get().await()
                val event = eventSnapshot.toObject(PublicEvent::class.java)

                event?.photos = getEventPhotos(eventSnapshot)
                eventPhotoRepository.getAllEventPhotos(eventId).forEach { storageReference ->
                    event?.photos?.forEach { photo ->
                        if (photo != null && storageReference.path.contains(photo.id)) {
                            photo.storageReference = storageReference
                        }
                    }
                }

                emit(Response.Success(event))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    private suspend fun getAllEventsList(): List<PublicEvent?> {
        val eventsSnapshot = eventsRef.get().await()
        return eventsSnapshot.documents.map {
            it.toObject(PublicEvent::class.java)?.apply {
                photos = getEventPhotos(it).apply {
                    setFirstEventPhotoStorageReference(it, this)
                }
            }
        }
    }

    // TODO: These two have to be refactored and moved to EventPhotoRepository
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