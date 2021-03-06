package com.ubb.citizen_u.data.model.citizens.requests

import com.google.firebase.firestore.DocumentId
import com.ubb.citizen_u.data.model.Photo
import com.ubb.citizen_u.data.model.citizens.Citizen
import com.ubb.citizen_u.data.model.citizens.Comment
import java.util.*

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
    citizen: Citizen? = null,
    photos: MutableList<Photo?> = mutableListOf(),
    var category: String? = null,
    var address: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var neighborhood: String? = null,
) : CitizenRequest(
    citizen = citizen,
    description = description,
    headline = headline,
    sentDate = sentDate,
    photos = photos
) {

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
        return "${super.toString()}, address = $address, latitude = $latitude, longitude = $longitude, neighborhood = $neighborhood"
    }
}

enum class RequestStatus {
    SENT, UNDER_REVIEW, IN_PROGRESS, COMPLETED, REJECTED,
}