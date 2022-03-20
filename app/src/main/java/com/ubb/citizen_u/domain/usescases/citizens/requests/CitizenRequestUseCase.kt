package com.ubb.citizen_u.domain.usescases.citizens.requests

data class CitizenRequestUseCase(
    val reportIncidentUseCase: ReportIncidentUseCase,
    val getCitizenReportedIncidents: GetCitizenReportedIncidents,
    val getOthersReportedIncidents: GetOthersReportedIncidents,
    val getReportedIncident: GetReportedIncident,
    val addCommentToIncident: AddCommentToIncident,
)