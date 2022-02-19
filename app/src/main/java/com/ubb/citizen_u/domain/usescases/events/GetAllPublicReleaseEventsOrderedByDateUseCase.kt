package com.ubb.citizen_u.domain.usescases.events

import com.ubb.citizen_u.data.repositories.EventRepository
import javax.inject.Inject

class GetAllPublicReleaseEventsOrderedByDateUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke() =
        eventRepository.getAllPublicReleaseEventsOrderedByDate()
}
