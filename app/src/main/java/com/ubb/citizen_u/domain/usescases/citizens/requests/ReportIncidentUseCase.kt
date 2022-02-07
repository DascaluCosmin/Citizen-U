package com.ubb.citizen_u.domain.usescases.citizens.requests

import android.net.Uri
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import javax.inject.Inject

class ReportIncidentUseCase @Inject constructor(
    private val citizenRequestRepository: CitizenRequestRepository
) {

    suspend operator fun invoke(
        incident: Incident,
        citizenId: String,
        listIncidentPhotoUri: List<Uri>
    ) =
        citizenRequestRepository.addIncident(incident, citizenId, listIncidentPhotoUri)
}
