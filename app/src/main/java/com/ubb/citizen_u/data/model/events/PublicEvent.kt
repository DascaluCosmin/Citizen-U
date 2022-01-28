package com.ubb.citizen_u.data.model.events

import com.google.firebase.firestore.DocumentId
import java.util.*

abstract class Event(
    @DocumentId var id: String = "",
    var title: String? = null,
    var category: String? = null,
    var content: String? = null,
    var photos: MutableList<EventPhoto?> = mutableListOf()
) {

    override fun toString(): String {
        return "ID = $id, Title = $title"
    }
}

class PublicEvent(
    var startDate: Date? = null,
    var endDate: Date? = null,
    var address: String? = null,
    var location: String? = null,
) : Event() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PublicEvent

        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false
        if (address != other.address) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startDate?.hashCode() ?: 0
        result = 31 * result + (endDate?.hashCode() ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "${super.toString()}, Location = $location, Address = $address"
    }
}