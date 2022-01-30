package com.ubb.citizen_u.domain.usescases.event

import com.ubb.citizen_u.data.repositories.EventRepository
import javax.inject.Inject

class GetAllPublicEventsUseCase @Inject constructor(
    private val eventsRepository: EventRepository
) {

    suspend operator fun invoke() =
        eventsRepository.getAllPublicEvents()
}