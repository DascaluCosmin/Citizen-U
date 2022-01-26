package com.ubb.citizen_u.domain.usescases.event

data class EventUseCases(
    val getAllEventsUseCase: GetAllEventsUseCase,
    val getAllEventsOrderedByDateUseCase: GetAllEventsOrderedByDateUseCase
) {
}