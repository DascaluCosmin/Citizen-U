package com.ubb.citizen_u.data.model

import android.net.Uri
import com.google.firebase.firestore.DocumentId
import com.google.firebase.storage.StorageReference

open class Attachment(
    @DocumentId var id: String = "",
    var name: String? = null,
    var description: String? = null,
) {
    override fun toString(): String {
        return "ID = $id, name = $name, description = $description"
    }
}

open class AttachmentData(
    var storageReference: StorageReference? = null,
    var uri: Uri? = null,
) : Attachment() {

    override fun toString(): String {
        return "${super.toString()}, uri = ${uri?.path}, storage reference = $storageReference"
    }
}

class Photo : AttachmentData()

class Pdf : AttachmentData()