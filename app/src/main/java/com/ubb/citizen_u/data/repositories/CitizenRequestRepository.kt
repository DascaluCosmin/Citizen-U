package com.ubb.citizen_u.data.repositories

import android.net.Uri
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface CitizenRequestRepository {

    suspend fun addIncident(incident: Incident, citizenId: String, listIncidentPhotoUri: List<Uri>): Flow<Response<Void?>>
}