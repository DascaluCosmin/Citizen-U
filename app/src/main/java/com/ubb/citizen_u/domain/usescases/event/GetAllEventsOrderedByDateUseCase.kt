package com.ubb.citizen_u.domain.usescases.event

import com.ubb.citizen_u.data.repositories.EventRepository
import javax.inject.Inject

class GetAllEventsOrderedByDateUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    suspend operator fun invoke() =
        eventRepository.getAllEventsOrderedByDate()
}