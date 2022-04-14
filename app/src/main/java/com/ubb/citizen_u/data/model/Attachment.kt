package com.ubb.citizen_u.data.model

import android.net.Uri
import com.google.firebase.firestore.DocumentId
import com.google.firebase.storage.StorageReference

open class Attachment(
    @DocumentId var id: String = "",
    var name: String? = null,
    var description: String? = null,
    var category: String? = null,
) {
    override fun toString(): String {
        return "ID = $id, name = $name, description = $description, category = $category"
    }
}

open class AttachmentData(
    category: String? = null,
    var storageReference: StorageReference? = null,
    var uri: Uri? = null,
) : Attachment(category = category) {

    override fun toString(): String {
        return "${super.toString()}, uri = ${uri?.path}, storage reference = $storageReference"
    }
}

class Photo(
    category: String? = null,
    uri: Uri? = null,
) : AttachmentData(category = category, uri = uri)

class Pdf : AttachmentData()