package com.ubb.citizen_u.domain.usescases.events

import com.ubb.citizen_u.data.repositories.EventRepository
import javax.inject.Inject

class GetPublicEventDetailsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(eventId: String) =
        eventRepository.getPublicEventDetails(eventId)
}
