package com.ubb.citizen_u.data.model.events

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Event(
    @DocumentId var id: String = "",
    var title: String? = null,
    var address: String? = null,
    var location: String? = null,
    var startDate: Date? = null,
    var endDate: Date? = null,
    var photos: MutableList<EventPhoto?>? = mutableListOf()
) {
}
