package com.ubb.citizen_u.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.storage.StorageReference

open class Attachment(
    @DocumentId var id: String = "",
    var name: String? = null,
    var storageReference: StorageReference? = null,
) {
}

class Photo : Attachment()

class Pdf : Attachment()