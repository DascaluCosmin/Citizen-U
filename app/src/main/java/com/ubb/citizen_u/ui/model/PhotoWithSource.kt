package com.ubb.citizen_u.ui.model

import com.ubb.citizen_u.data.model.Photo

data class PhotoWithSource(
    var photo: Photo?,
    var source: Source,
) {

    override fun toString(): String {
        return "Photo: ${photo?.id} - ${photo?.name}, source: $source"
    }
}

enum class Source {
    UPLOAD, CAMERA
}