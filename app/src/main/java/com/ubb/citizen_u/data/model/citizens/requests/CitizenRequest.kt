package com.ubb.citizen_u.data.model.citizens.requests

import com.google.firebase.firestore.DocumentId
import com.ubb.citizen_u.data.model.Photo
import java.util.*

// TODO: properties - title?
abstract class CitizenRequest(
    @DocumentId var id: String = "",
    var description: String? = null,
    var sentDate: Date? = null,
    var status: RequestStatus = RequestStatus.SENT,
    var photos: MutableList<Photo?> = mutableListOf(),
) {

    override fun toString(): String {
        return "ID = $id, description = $description, date = $sentDate"
    }
}

class Incident(
    description: String? = null,
    sentDate: Date? = null,
    val address: String? = null,
) : CitizenRequest(description = description, sentDate = sentDate) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Incident

        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        return address?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "${super.toString()}, address = $address"
    }
}

enum class RequestStatus {
    SENT, UNDER_REVIEW, IN_PROGRESS, COMPLETED, REJECTED,
}
