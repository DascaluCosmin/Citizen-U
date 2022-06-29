package com.ubb.citizen_u.data.repositories

import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.Comment
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface CitizenRequestRepository {

    suspend fun addIncident(
        incident: Incident,
        listIncidentPhotos: List<Photo>,
    ): Flow<Response<Boolean>>

    suspend fun getIncident(citizenId: String, incidentId: String): Flow<Response<Incident?>>

    suspend fun getAllIncidents(citizenId: String): Flow<Response<List<Incident?>>>

    suspend fun getAllIncidentsByCategory(incidentCategory: String?): Flow<Response<List<Incident?>>>

    suspend fun getAllIncidentsOfOthers(currentCitizenId: String): Flow<Response<List<Incident?>>>

    suspend fun addCommentToIncident(incident: Incident, comment: Comment): Flow<Response<Boolean>>

    suspend fun getAllIncidentCategories(): Flow<Response<List<String>>>
}