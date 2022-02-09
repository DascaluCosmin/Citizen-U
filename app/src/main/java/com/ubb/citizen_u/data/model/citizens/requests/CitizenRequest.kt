package com.ubb.citizen_u.data.model.citizens.requests

import com.google.firebase.firestore.DocumentId
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
