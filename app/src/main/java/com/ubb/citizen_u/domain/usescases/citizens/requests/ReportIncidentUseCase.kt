package com.ubb.citizen_u.domain.usescases.citizens.requests

import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import javax.inject.Inject

class ReportIncidentUseCase @Inject constructor(
    private val citizenRequestRepository: CitizenRequestRepository,
) {

    suspend operator fun invoke(
        incident: Incident,
        listIncidentPhotos: List<Photo>,
    ) =
        citizenRequestRepository.addIncident(incident, listIncidentPhotos)
}
