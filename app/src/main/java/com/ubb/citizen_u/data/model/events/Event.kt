package com.ubb.citizen_u.data.model.events

import java.util.*

data class Event(
    val id: String,
    val title: String,
    val address: String,
    val location: String,
    val startDate: Date,
    val endDate: Date,
) {

}
