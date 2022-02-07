package com.ubb.citizen_u.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.storage.StorageReference

data class Photo(
    @DocumentId var id: String = "",
    var name: String? = null,
    var storageReference: StorageReference? = null
) {
}