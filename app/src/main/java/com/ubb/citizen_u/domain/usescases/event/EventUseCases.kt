package com.ubb.citizen_u.domain.usescases.event

data class EventUseCases(
    val getAllPublicEventsUseCase: GetAllPublicEventsUseCase,
    val getAllPublicEventsOrderedByDateUseCase: GetAllPublicEventsOrderedByDateUseCase,
    val getPublicEventDetailsUseCase: GetPublicEventDetailsUseCase,

    val getAllCouncilMeetEventsUseCase: GetAllCouncilMeetEventsUseCase,
    val getAllCouncilMeetEventsOrderedByUseCase: GetAllCouncilMeetEventsOrderedByDateUseCase,
) {
}