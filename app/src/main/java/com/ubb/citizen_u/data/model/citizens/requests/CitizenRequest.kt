package com.ubb.citizen_u.data.model.citizens.requests

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.ubb.citizen_u.data.model.Photo
import java.util.*

// TODO: properties - title?
abstract class CitizenRequest(
    @DocumentId var id: String = "",
    var description: String? = null,
    var sentDate: Date? = null,
) {

    override fun toString(): String {
        return "ID = $id, description = $description, date = $sentDate"
    }
}

class Incident(
    description: String?,
    sentDate: Date? = null,
) : CitizenRequest(description = description, sentDate = sentDate) {

    var photos: MutableList<Photo?> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Incident

        if (photos != other.photos) return false

        return true
    }

    override fun hashCode(): Int {
        return photos.hashCode()
    }

    override fun toString(): String {
        return "${super.toString()}, number of photos = ${photos.size}"
    }
}
