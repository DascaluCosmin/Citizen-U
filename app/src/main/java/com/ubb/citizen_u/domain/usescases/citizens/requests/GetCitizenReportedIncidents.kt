package com.ubb.citizen_u.domain.usescases.citizens.requests

import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import javax.inject.Inject

class GetCitizenReportedIncidents @Inject constructor(
    private val citizenRequestRepository: CitizenRequestRepository,
) {
    suspend operator fun invoke(citizenId: String) =
        citizenRequestRepository.getAllIncidents(citizenId)
}
