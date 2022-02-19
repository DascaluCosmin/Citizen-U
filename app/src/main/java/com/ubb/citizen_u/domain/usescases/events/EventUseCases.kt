package com.ubb.citizen_u.domain.usescases.events

data class EventUseCases(
    val getAllPublicEventsUseCase: GetAllPublicEventsUseCase,
    val getAllPublicEventsOrderedByDateUseCase: GetAllPublicEventsOrderedByDateUseCase,
    val getPublicEventDetailsUseCase: GetPublicEventDetailsUseCase,

    val getAllPublicReleaseEvents: GetAllPublicReleaseEvents,
    val getAllPublicReleaseEventsOrderedByUseCase: GetAllPublicReleaseEventsOrderedByDateUseCase,
) {
}