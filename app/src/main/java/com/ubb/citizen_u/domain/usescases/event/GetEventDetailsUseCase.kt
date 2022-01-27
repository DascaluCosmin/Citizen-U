package com.ubb.citizen_u.domain.usescases.event

import com.ubb.citizen_u.data.repositories.EventRepository
import javax.inject.Inject

class GetEventDetailsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(eventId: String) =
        eventRepository.getEventDetails(eventId)
}
