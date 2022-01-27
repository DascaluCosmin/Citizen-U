package com.ubb.citizen_u.data.model.events

import com.google.firebase.firestore.DocumentId
import com.google.firebase.storage.StorageReference

data class EventPhoto(
    @DocumentId var id: String = "",
    var name: String? = null,
    var storageReference: StorageReference? = null
) {
}