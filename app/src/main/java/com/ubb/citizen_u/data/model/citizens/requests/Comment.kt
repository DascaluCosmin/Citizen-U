package com.ubb.citizen_u.data.model.citizens.requests

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Comment(
    @DocumentId var id: String = "",
    var text: String? = null,
    var postedOn: Date? = null,
    var userFirstName: String? = null,
    var userLastName: String? = null,
    var userId: String? = null,
) {

    override fun toString(): String {
        return "ID = $id, Posted on = $postedOn by $userFirstName $userLastName (id: $userId), text = $text"
    }
}