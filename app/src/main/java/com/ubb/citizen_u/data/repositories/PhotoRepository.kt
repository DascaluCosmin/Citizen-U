package com.ubb.citizen_u.data.repositories

import android.net.Uri
import com.google.firebase.storage.StorageReference
import com.ubb.citizen_u.data.model.Photo

interface PhotoRepository {

    suspend fun getMainEventPhotoStorageReference(
        eventId: String,
        photoId: String
    ): StorageReference

    suspend fun getAllEventPhotos(eventId: String): List<StorageReference>

    suspend fun getAllIncidentPhotos(citizenId: String, incidentId: String): MutableList<Photo?>

    suspend fun getAllProposedProjectPhotos(citizenId: String, proposedProjectId: String): MutableList<Photo?>

    suspend fun saveIncidentPhotos(
        listIncidentPhotoUri: List<Uri>,
        incidentId: String,
        citizenId: String
    ): Boolean
}