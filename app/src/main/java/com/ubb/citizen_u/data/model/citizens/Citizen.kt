package com.ubb.citizen_u.data.model.citizens

import com.google.firebase.firestore.DocumentId

data class Citizen(
    @DocumentId var id: String = "",
    var firstName: String? = null,
    var lastName: String? = null
) {

    override fun toString(): String {
        return "ID = $id, name = $firstName $lastName"
    }
}
