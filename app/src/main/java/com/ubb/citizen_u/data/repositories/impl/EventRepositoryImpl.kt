package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.events.PeriodicEvent
import com.ubb.citizen_u.data.model.events.PublicEvent
import com.ubb.citizen_u.data.model.events.PublicReleaseEvent
import com.ubb.citizen_u.data.repositories.EventRepository
import com.ubb.citizen_u.data.repositories.PhotoRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import com.ubb.citizen_u.util.DatabaseConstants.PHOTOS_COL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val publicEventsRef: CollectionReference,
    private val publicReleaseEventsRef: CollectionReference,
    private val photoRepository: PhotoRepository,
) : EventRepository {

    override suspend fun getAllPublicEvents(): Flow<Response<List<PublicEvent?>>> =
        flow {
            try {
                emit(Response.Loading)

                val events = getAllPublicEventsList()
                emit(Response.Success(events))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllEventsOrderedByDate(): Flow<Response<List<PublicEvent?>>> =
        flow {
            try {
                emit(Response.Loading)

                val sortedEvents = getAllPublicEventsList().sortedBy {
                    it?.startDate
                }.filter {
                    val oneWeekAgoDate = Date().apply {
                        time -= 2 * 7 * 24 * 60 * 60 * 1000
                    }

                    it?.startDate!!.after(oneWeekAgoDate)
                }
                emit(Response.Success(sortedEvents))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getPublicEvent(eventId: String): Flow<Response<PublicEvent?>> =
        flow {
            try {
                emit(Response.Loading)

                val eventSnapshot = publicEventsRef.document(eventId).get().await()
                val event = eventSnapshot.toObject(PublicEvent::class.java)

                event?.photos = getEventPhotos(eventSnapshot)
                photoRepository.getAllEventPhotos(eventId).forEach { storageReference ->
                    event?.photos?.forEach { photo ->
                        photo?.apply {
                            if (storageReference.path.contains(id)) {
                                this.storageReference = storageReference
                            }
                        }
                    }
                }

                emit(Response.Success(event))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllPublicReleaseEvents(): Flow<Response<List<PublicReleaseEvent?>>> =
        flow {
            try {
                emit(Response.Loading)

                val publicReleaseEvents = getAllPublicReleaseEventsList()
                emit(Response.Success(publicReleaseEvents))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllPeriodicEvents(): Flow<Response<List<PeriodicEvent?>>> =
        flow {
            try {
                emit(Response.Loading)

                val periodicEvents = getAllPeriodicEventsList().filterNotNull()
                emit(Response.Success(periodicEvents))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllPublicReleaseEventsOrderedByDate(): Flow<Response<List<PublicReleaseEvent?>>> =
        flow {
            try {
                emit(Response.Loading)
                val sortedPublicReleaseEvents = getAllPublicReleaseEventsList().sortedByDescending {
                    it?.publicationDate
                }
//                sortedPublicReleaseEvents.forEach {
//                    it?.photos?.apply {
//                        if (size > 0) {
//                            val firstPhotoId = this[0]?.id
//                            if (firstPhotoId != null) {
//                                eventPhotoRepository.getMainEventPhotoStorageReference(
//                                    it.id,
//                                    firstPhotoId
//                                )
//                            }
//                        }
//                    }
//                }
                emit(Response.Success(sortedPublicReleaseEvents))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))

            }
        }

    override suspend fun getPublicReleaseEvent(eventId: String): Flow<Response<PublicReleaseEvent?>> =
        flow {
            try {
                emit(Response.Loading)

                val eventSnapshot = publicReleaseEventsRef.document(eventId).get().await()
                val event = eventSnapshot.toObject(PublicReleaseEvent::class.java)

                event?.photos = getEventPhotos(eventSnapshot)
                photoRepository.getAllEventPhotos(eventId).forEach { storageReference ->
                    event?.photos?.forEach { photo ->
                        photo?.apply {
                            if (storageReference.path.contains(id)) {
                                this.storageReference = storageReference
                            }
                        }
                    }
                }
                emit(Response.Success(event))
            } catch (exception: Exception) {
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }


    private suspend fun getAllPublicEventsList(): List<PublicEvent?> {
        val eventsSnapshot = publicEventsRef.get().await()
        return eventsSnapshot.documents.map {
            it.toObject(PublicEvent::class.java)?.apply {
                photos = getEventPhotos(it).apply {
                    setFirstEventPhotoStorageReference(it, this)
                }
            }
        }
    }

    private suspend fun getAllPeriodicEventsList(): List<PeriodicEvent?> {
        val eventsSnapshot = publicReleaseEventsRef.get().await()
        return eventsSnapshot.documents.map {
            val periodicEvent = it.toObject(PeriodicEvent::class.java)
            if (periodicEvent?.frequency == null) {
                null
            } else {
                periodicEvent.apply {
                    photos = getEventPhotos(it).apply {
                        setFirstEventPhotoStorageReference(it, this)
                    }
                }
            }
        }
    }

    private suspend fun getAllPublicReleaseEventsList(): List<PublicReleaseEvent?> {
        val eventsSnapshot = publicReleaseEventsRef.get().await()
        return eventsSnapshot.documents.map {
            it.toObject(PublicReleaseEvent::class.java)?.apply {
                photos = getEventPhotos(it).apply {
                    setFirstEventPhotoStorageReference(it, this)
                }
            }
        }
    }


    // TODO: These two have to be refactored and moved to EventPhotoRepository
    private suspend fun setFirstEventPhotoStorageReference(
        eventsDocSnapshot: DocumentSnapshot, photos: MutableList<Photo?>,
    ) {
        if (photos.size > 0) {
            val firstPhotoId = photos[0]?.id
            if (firstPhotoId != null) {
                photos[0]?.storageReference =
                    photoRepository.getMainEventPhotoStorageReference(
                        eventsDocSnapshot.id, firstPhotoId
                    )
            }
        }
    }

    private suspend fun getEventPhotos(eventDocSnapshot: DocumentSnapshot): MutableList<Photo?> {
        val eventPhotosSnapshot =
            eventDocSnapshot.reference.collection(PHOTOS_COL).get().await()

        val eventPhotos = eventPhotosSnapshot.documents.map {
            it.toObject(Photo::class.java)
        }.toMutableList()

        return eventPhotos
    }
}