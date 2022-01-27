package com.ubb.citizen_u.data.repositories.impl

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ubb.citizen_u.data.repositories.EventPhotoRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventPhotoRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) : EventPhotoRepository {

    companion object {
        private const val FIREBASE_STORAGE_EVENTS_IMAGES = "images/events"
    }

    override suspend fun getMainEventPhotoStorageReference(
        eventId: String,
        photoId: String
    ): StorageReference {
        val pathToEventPhoto = "$FIREBASE_STORAGE_EVENTS_IMAGES/$eventId/$photoId.jpg"
        return firebaseStorage.reference.child(pathToEventPhoto)
    }

    override suspend fun getAllEventPhotos(eventId: String): List<StorageReference> {
        val pathToEventPhotosFolder = "$FIREBASE_STORAGE_EVENTS_IMAGES/$eventId/"
        val result = firebaseStorage.reference.child(pathToEventPhotosFolder)
            .listAll().await()
        return result.items
    }
}