package com.ubb.citizen_u.domain.usescases.event

data class EventUseCases(
    val getAllPublicPublicEventsUseCase: GetAllPublicEventsUseCase,
    val getAllPublicPublicEventsOrderedByDateUseCase: GetAllPublicEventsOrderedByDateUseCase,
    val getPublicEventDetailsUseCase: GetPublicEventDetailsUseCase,
) {
}