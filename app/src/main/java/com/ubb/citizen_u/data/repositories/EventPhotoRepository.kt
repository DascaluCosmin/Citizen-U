package com.ubb.citizen_u.data.repositories

import com.google.firebase.storage.StorageReference
import com.ubb.citizen_u.data.model.events.EventPhoto

interface EventPhotoRepository {

    suspend fun getMainEventPhotoStorageReference(eventId: String, photoId: String): StorageReference

    suspend fun getAllEventPhotos(eventId: String): List<EventPhoto>
}