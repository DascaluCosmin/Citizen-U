package com.ubb.citizen_u.data.model.citizens

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Citizen(
    @DocumentId var id: String = "",
    var cnp: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var address: String? = null,
) {

    override fun toString(): String {
        return "ID = $id, name = $firstName $lastName, CNP = $cnp"
    }

    @Exclude
    fun getFullName(): String {
        return "$firstName $lastName"
    }
}
