package com.ubb.citizen_u.data.repositories.impl

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.ubb.citizen_u.data.Details
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.data.model.citizens.requests.IncidentCategory
import com.ubb.citizen_u.data.repositories.CitizenRepository
import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import com.ubb.citizen_u.data.repositories.PhotoRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import com.ubb.citizen_u.util.DatabaseConstants.COMMENTS_COL
import com.ubb.citizen_u.util.DatabaseConstants.USER_REQUESTS_INCIDENTS_COL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CitizenRequestRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
    private val photoRepository: PhotoRepository,
    private val citizenRepository: CitizenRepository,
) : CitizenRequestRepository {

    companion object {
        private const val TAG = "UBB-CitizenRequestRepositoryImpl"
    }

    // TODO: Transaction this
    override suspend fun addIncident(
        incident: Incident,
        citizenId: String,
        listIncidentPhotoUri: List<Uri>,
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

    override suspend fun getIncident(
        citizenId: String,
        incidentId: String,
    ): Flow<Response<Incident?>> =
        flow {
            try {
                emit(Response.Loading)

                val incidentSnapshot = usersRef.document(citizenId)
                    .collection(USER_REQUESTS_INCIDENTS_COL)
                    .document(incidentId).get().await()
                val incident = getIncident(
                    incidentDocSnapshot = incidentSnapshot,
                    citizenId = citizenId,
                    listDetails = listOf(Details.PHOTOS, Details.COMMENTS))
                emit(Response.Success(incident))
            } catch (exception: Exception) {
                Log.d(TAG, "getIncident: An error as occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllIncidents(citizenId: String): Flow<Response<List<Incident?>>> =
        flow {
            try {
                emit(Response.Loading)

                val incidents = getAllIncidentsList(
                    citizenId = citizenId,
                    listDetails = listOf(Details.PHOTOS)
                )
                emit(Response.Success(incidents))
            } catch (exception: Exception) {
                Log.d(TAG, "getAllIncidentsOfCitizen: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllIncidents(incidentCategory: IncidentCategory?): Flow<Response<List<Incident?>>> =
        flow {
            try {
                emit(Response.Loading)

                val incidents = getAllIncidentsList()
                emit(Response.Success(incidents))
            } catch (exception: Exception) {
                Log.d(TAG, "getAllIncidents: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllIncidentsOfOthers(currentCitizenId: String): Flow<Response<List<Incident?>>> =
        flow {
            try {
                emit(Response.Loading)

                val incidents = getAllIncidentsOfOthersList(currentCitizenId)
                emit(Response.Success(incidents))
            } catch (exception: Exception) {
                Log.d(TAG, "getAllIncidentsOfOthers: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun addCommentToIncident(
        incident: Incident,
        comment: Comment,
    ): Flow<Response<Boolean>> = flow {
        try {
            emit(Response.Loading)
            if (incident.citizen == null) {
                emit(Response.Error(DEFAULT_ERROR_MESSAGE))
                return@flow
            }

            usersRef.document(incident.citizen!!.id)
                .collection(USER_REQUESTS_INCIDENTS_COL)
                .document(incident.id)
                .collection(COMMENTS_COL)
                .add(comment)
                .await()

            emit(Response.Success(true))
        } catch (exception: Exception) {
            Log.d(TAG, "addCommentToIncident: An error has occurred: ${exception.message}")
            emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
        }
    }

    private suspend fun getAllIncidentsOfOthersList(currentCitizenId: String): List<Incident?> {
        val usersSnapshot = usersRef.get().await()
        return usersSnapshot.documents
            .filterNot { it.id == currentCitizenId }
            .map {
                getAllIncidentsList(
                    citizenId = it.id,
                    listDetails = listOf(Details.PHOTOS)
                )
            }.flatten()
    }

    private suspend fun getAllIncidentsList(): List<Incident?> {
        val usersSnapshot = usersRef.get().await()
        return usersSnapshot.documents
            .map {
                getAllIncidentsList(
                    citizenId = it.id,
                    listDetails = listOf()
                )
            }.flatten()
    }

    private suspend fun getAllIncidentsList(
        citizenId: String,
        listDetails: List<Details>,
    ): List<Incident?> {
        val incidentSnapshot =
            usersRef.document(citizenId).collection(USER_REQUESTS_INCIDENTS_COL).get().await()
        return incidentSnapshot.documents.map {
            getIncident(
                incidentDocSnapshot = it,
                citizenId = citizenId,
                listDetails = listDetails)
        }
    }

    private suspend fun getIncident(
        incidentDocSnapshot: DocumentSnapshot,
        citizenId: String,
        listDetails: List<Details>,
    ): Incident? {
        return incidentDocSnapshot.toObject(Incident::class.java)?.apply {
            if (listDetails.contains(Details.COMMENTS)) {
                comments = getIncidentComments(incidentDocSnapshot = incidentDocSnapshot)
            }

            if (listDetails.contains(Details.PHOTOS)) {
                photos = photoRepository.getAllIncidentPhotos(citizenId, incidentId = id)
            }

            citizenRepository.getCitizen(citizenId).collect { citizenResponse ->
                if (citizenResponse is Response.Success) {
                    this.citizen = citizenResponse.data
                }
            }
        }
    }

    private suspend fun getIncidentComments(incidentDocSnapshot: DocumentSnapshot): MutableList<Comment?> {
        val incidentCommentsSnapshot = incidentDocSnapshot.reference
            .collection(COMMENTS_COL).get().await()
        return incidentCommentsSnapshot.documents.map {
            it.toObject(Comment::class.java)
        }.toMutableList()
    }
}