package com.ubb.citizen_u.data.repositories.impl

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.ubb.citizen_u.data.Details
import com.ubb.citizen_u.data.api.Address
import com.ubb.citizen_u.data.api.AddressApi
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.data.repositories.CitizenRepository
import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import com.ubb.citizen_u.data.repositories.CommentRepository
import com.ubb.citizen_u.data.repositories.PhotoRepository
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.util.CitizenRequestConstants.DEFAULT_INCIDENT_CATEGORY
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE
import com.ubb.citizen_u.util.DatabaseConstants.COMMENTS_COL
import com.ubb.citizen_u.util.DatabaseConstants.USER_REQUESTS_INCIDENTS_COL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CitizenRequestRepositoryImpl @Inject constructor(
    private val usersRef: CollectionReference,
    private val incidentCategoriesRef: CollectionReference,
    private val photoRepository: PhotoRepository,
    private val citizenRepository: CitizenRepository,
    private val commentRepository: CommentRepository,
    private val addressApi: AddressApi,
) : CitizenRequestRepository {

    companion object {
        private const val TAG = "UBB-CitizenRequestRepositoryImpl"
    }

    // TODO: Transaction this
    override suspend fun addIncident(
        incident: Incident,
        listIncidentPhotos: List<Photo>,
    ): Flow<Response<Boolean>> =
        flow {
            if (listIncidentPhotos.isEmpty()) {
                emit(Response.Error("Please provide a photo of the incident!"))
                return@flow
            }
            if (incident.citizen == null) {
                emit(Response.Error("The citizen value can't be null!"))
                return@flow
            }

            try {
                emit(Response.Loading)

                incident.category = getIncidentOverallCategory(listIncidentPhotos)
                    ?.replace('_', ' ')
                incident.neighborhood = getIncidentAddress(incident)?.suburb

                Log.d(TAG, "The overall predicted category is ${incident.category}")

                val citizen = incident.citizen
                val result =
                    usersRef.document(citizen!!.id).collection(USER_REQUESTS_INCIDENTS_COL)
                        .add(incident)
                        .await()

                val listIncidentPhotoUri =
                    listIncidentPhotos.mapNotNull { it.uri }.toList()
                photoRepository.saveIncidentPhotos(
                    listIncidentPhotoUri = listIncidentPhotoUri,
                    incidentId = result.id,
                    citizenId = citizen.id
                )
                emit(Response.Success(true))
            } catch (exception: Exception) {
                Log.d(TAG, "addIncident: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    private fun getIncidentOverallCategory(listIncidentPhotos: List<Photo>): String? {
        val photosGroupedByCategory = listIncidentPhotos.groupBy { it.category }

        var mostPredictedCategory: String? = ""
        var numberAppearancesMostPredictedCategory = 0
        photosGroupedByCategory.forEach {
            if (it.value.size > numberAppearancesMostPredictedCategory) {
                mostPredictedCategory = it.key
                numberAppearancesMostPredictedCategory = it.value.size
            }
        }

        var isAmbiguousPredictedCategoryFlag = -1
        photosGroupedByCategory.values.forEach {
            if (it.size == numberAppearancesMostPredictedCategory) {
                isAmbiguousPredictedCategoryFlag++
            }
        }

        return if (isAmbiguousPredictedCategoryFlag == 0) mostPredictedCategory else DEFAULT_INCIDENT_CATEGORY
    }

    private suspend fun getIncidentAddress(incident: Incident): Address? {
        if (incident.latitude == null || incident.longitude == null) {
            return null
        }

        val response = try {
            val response = addressApi.getAddress(
                latitude = incident.latitude!!,
                longitude = incident.longitude!!
            )
            response
        } catch (exception: IOException) {
            Log.e(TAG,
                "IOException thrown while getting incident suburb. There might be an internet connection issue: ${exception.message}")
            return null
        } catch (exception: HttpException) {
            Log.e(TAG,
                "HttpException thrown while getting incident suburb because of an unexpected result: ${exception.message()}")
            return null
        }
        return if (response.isSuccessful && response.body() != null) {
            response.body()?.address
        } else {
            Log.e(TAG, "An error has occurred. The result is ${response.isSuccessful}")
            null
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
                ).sortedByDescending {
                    it?.sentDate
                }
                emit(Response.Success(incidents))
            } catch (exception: Exception) {
                Log.d(TAG, "getAllIncidentsOfCitizen: An error has occurred: ${exception.message}")
                emit(Response.Error(exception.message ?: DEFAULT_ERROR_MESSAGE))
            }
        }

    override suspend fun getAllIncidentsByCategory(incidentCategory: String?): Flow<Response<List<Incident?>>> =
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

    override suspend fun getAllIncidentCategories(): Flow<Response<List<String>>> =
        flow {
            try {
                emit(Response.Loading)
                val incidentCategoriesSnapshot = incidentCategoriesRef.get().await()
                val incidentCategories = incidentCategoriesSnapshot.documents
                    .map {
                        it.id
                    }
                    .toList()
                emit(Response.Success(incidentCategories))
            } catch (exception: Exception) {
                Log.e(TAG, "getAllIncidentCategories: An error has occurred: ${exception.message}")
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
            .sortedByDescending {
                it?.sentDate
            }
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
                comments = commentRepository.getAllComments(incidentDocSnapshot)
//                comments = getIncidentComments(incidentDocSnapshot = incidentDocSnapshot)
            }

            if (listDetails.contains(Details.PHOTOS)) {
                photos = photoRepository.getAllIncidentPhotos(citizenId, incidentId = id)
            }

            citizenRepository.getCitizen(citizenId).collect { citizenResponse ->
                if (citizenResponse is Response.Success) {
                    this.citizen = citizenResponse.data
                }
            }
            updateSolveOfflineConflict(this)
        }
    }

    private suspend fun getIncidentComments(incidentDocSnapshot: DocumentSnapshot): MutableList<Comment?> {
        val incidentCommentsSnapshot = incidentDocSnapshot.reference
            .collection(COMMENTS_COL).get().await()
        return incidentCommentsSnapshot.documents.map {
            it.toObject(Comment::class.java)
        }.toMutableList()
    }

    private suspend fun updateSolveOfflineConflict(incident: Incident) {
        val needsUpdate =
            (incident.neighborhood.isNullOrEmpty() || incident.address.isNullOrEmpty())
        if (needsUpdate) {
            val address = getIncidentAddress(incident)
            address?.let {
                if (incident.neighborhood.isNullOrEmpty()) {
                    incident.neighborhood = it.suburb
                }
                if (incident.address.isNullOrEmpty()) {
                    incident.address = "${it.road}, ${it.house_number}"
                }
                usersRef.document(incident.citizen!!.id)
                    .collection(USER_REQUESTS_INCIDENTS_COL)
                    .document(incident.id)
                    .update("neighborhood", incident.neighborhood,
                        "address", incident.address
                    )
            }
        }
    }
}