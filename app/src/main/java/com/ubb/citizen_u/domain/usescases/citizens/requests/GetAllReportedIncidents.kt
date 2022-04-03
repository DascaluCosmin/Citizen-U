package com.ubb.citizen_u.domain.usescases.citizens.requests

import com.ubb.citizen_u.data.model.citizens.requests.IncidentCategory
import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import javax.inject.Inject

class GetAllReportedIncidents @Inject constructor(
    private val citizenRequestRepository: CitizenRequestRepository
) {
    suspend operator fun invoke(incidentCategory: IncidentCategory?) =
        citizenRequestRepository.getAllIncidents(incidentCategory)
}
