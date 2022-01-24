package com.ubb.citizen_u.data.model.events

import java.util.*

data class Event(
    var id: String? = null,
    val title: String? = null,
    val address: String? = null,
    val location: String? = null,
    val startDate: Date? = null,
    val endDate: Date? = null,
) {
}
