package com.ubb.citizen_u.data.model.citizens.requests

import com.google.firebase.firestore.DocumentId
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.Citizen
import com.ubb.citizen_u.data.model.citizens.Comment
import java.util.*

// TODO: properties - title?
abstract class CitizenRequest(
    @DocumentId var id: String = "",
    var citizen: Citizen? = null,
    var description: String? = null,
    var headline: String? = null,
    var sentDate: Date? = null,
    var status: RequestStatus = RequestStatus.SENT,
    var photos: MutableList<Photo?> = mutableListOf(),
    var comments: MutableList<Comment?> = mutableListOf(),
) {

    override fun toString(): String {
        return "ID = $id, description = $description, date = $sentDate, headline = $headline"
    }
}

class Incident(
    description: String? = null,
    headline: String? = null,
    sentDate: Date? = null,
    val category: IncidentCategory? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
) : CitizenRequest(description = description, headline = headline, sentDate = sentDate) {

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
        return "${super.toString()}, address = $address, latitude = $latitude, longitude = $longitude"
    }
}

enum class RequestStatus {
    SENT, UNDER_REVIEW, IN_PROGRESS, COMPLETED, REJECTED,
}

enum class IncidentCategory {
    PIPELINE, STREET_HOLE, FALLEN_TREE, UNCUT_GRASS, FALLEN_WIRES,
}
