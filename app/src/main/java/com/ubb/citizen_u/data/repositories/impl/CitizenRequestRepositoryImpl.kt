package com.ubb.citizen_u.data.repositories.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import com.ubb.citizen_u.data.repositories.PhotoRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import com.ubb.citizen_u.util.DatabaseConstants.USER_REQUESTS_INCIDENTS_COL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CitizenRequestRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
    private val photoRepository: PhotoRepository,
) : CitizenRequestRepository {

    companion object {
        private const val TAG = "CitizenRequestRepositoryImpl"
    }

    // TODO: Transction this
    override suspend fun addIncident(
        incident: Incident,
        citizenId: String,
        listIncidentPhotoUri: List<Uri>
    ): Flow<Response<Boolean>> =
        flow {
            if (listIncidentPhotoUri.isEmpty()) {
                emit(Response.Error("Please provide a photo of the incident!"))
                return@flow
            }

            try {
                emit(Response.Loading)

                val result =
                    usersRef.document(citizenId).collection(USER_REQUESTS_INCIDENTS_COL)
                        .add(incident)
                        .await()

                photoRepository.saveIncidentPhotos(
                    listIncidentPhotoUri = listIncidentPhotoUri,
                    incidentId = result.id,
                    citizenId = citizenId
                )
                emit(Response.Success(true))
            } catch (exception: Exception) {
                Log.d(TAG, "addIncident: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }
}