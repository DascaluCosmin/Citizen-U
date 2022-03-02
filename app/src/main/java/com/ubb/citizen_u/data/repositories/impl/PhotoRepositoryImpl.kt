package com.ubb.citizen_u.data.repositories.impl

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.repositories.PhotoRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
) : PhotoRepository {

    companion object {
        private const val TAG = "PhotoRepositoryImpl"
        private const val FIREBASE_STORAGE_EVENTS_IMAGES = "images/events"
        private const val FIREBASE_STORAGE_INCIDENT_REPORTS_IMAGES = "images/incident_reports"
        private const val PHOTO_FILE_EXTENSION = ".jpg"
    }

    override suspend fun getMainEventPhotoStorageReference(
        eventId: String,
        photoId: String,
    ): StorageReference {
        val pathToEventPhoto =
            "$FIREBASE_STORAGE_EVENTS_IMAGES/$eventId/$photoId$PHOTO_FILE_EXTENSION"
        return firebaseStorage.reference.child(pathToEventPhoto)
    }

    override suspend fun getAllEventPhotos(eventId: String): List<StorageReference> {
        val pathToEventPhotosFolder = "$FIREBASE_STORAGE_EVENTS_IMAGES/$eventId/"
        val result = firebaseStorage.reference.child(pathToEventPhotosFolder)
            .listAll().await()
        return result.items
    }

    override suspend fun getAllIncidentPhotos(
        citizenId: String,
        incidentId: String,
    ): MutableList<Photo?> {
        val pathToIncidentPhotosFolder =
            "$FIREBASE_STORAGE_INCIDENT_REPORTS_IMAGES/$citizenId/$incidentId/"
        val result = firebaseStorage.reference.child(pathToIncidentPhotosFolder)
            .listAll().await()
        return result.items.map { Photo(storageReference = it) }.toMutableList()
    }

    override suspend fun saveIncidentPhotos(
        listIncidentPhotoUri: List<Uri>,
        incidentId: String,
        citizenId: String,
    ): Boolean {
        val pathToCitizenIncidentReportsFolder =
            "$FIREBASE_STORAGE_INCIDENT_REPORTS_IMAGES/$citizenId/$incidentId"
        listIncidentPhotoUri.forEach { uri ->
            val result =
                firebaseStorage.getReference("$pathToCitizenIncidentReportsFolder/${uri.lastPathSegment}")
                    .putFile(uri).await()
            if (!result.task.isSuccessful) {
                return false
            }
        }
        return true
    }
}