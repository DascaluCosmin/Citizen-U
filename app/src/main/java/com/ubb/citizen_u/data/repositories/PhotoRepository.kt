package com.ubb.citizen_u.data.repositories

import android.net.Uri
import com.google.firebase.storage.StorageReference

interface PhotoRepository {

    suspend fun getMainEventPhotoStorageReference(
        eventId: String,
        photoId: String
    ): StorageReference

    suspend fun getAllEventPhotos(eventId: String): List<StorageReference>

    suspend fun saveIncidentPhotos(
        listIncidentPhotoUri: List<Uri>,
        incidentId: String,
        citizenId: String
    ): Boolean
}