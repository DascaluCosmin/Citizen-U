package com.ubb.citizen_u.domain.usescases.citizens.requests

import com.ubb.citizen_u.data.model.citizens.requests.Comment
import com.ubb.citizen_u.data.model.citizens.requests.Incident
import com.ubb.citizen_u.data.repositories.CitizenRequestRepository
import javax.inject.Inject

class AddCommentToIncident @Inject constructor(
    private val citizenRequestRepository: CitizenRequestRepository,
) {
    suspend operator fun invoke(incident: Incident, comment: Comment) =
        citizenRequestRepository.addCommentToIncident(incident, comment)
}
