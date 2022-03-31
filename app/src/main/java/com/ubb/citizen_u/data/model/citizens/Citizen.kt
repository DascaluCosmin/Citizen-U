package com.ubb.citizen_u.data.model.citizens

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Citizen(
    @DocumentId var id: String = "",
    var firstName: String? = null,
    var lastName: String? = null
) {

    override fun toString(): String {
        return "ID = $id, name = $firstName $lastName"
    }

    @Exclude
    fun getFullName(): String {
        return "$firstName $lastName"
    }
}
