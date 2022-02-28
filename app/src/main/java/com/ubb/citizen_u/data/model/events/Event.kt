package com.ubb.citizen_u.data.model.events

import com.google.firebase.firestore.DocumentId
import com.ubb.citizen_u.data.model.Photo
import java.util.*

abstract class Event(
    @DocumentId var id: String = "",
    var title: Map<String, String> = mapOf(),
    var category: Map<String, String> = mapOf(),
    var content: Map<String, String> = mapOf(),
    var photos: MutableList<Photo?> = mutableListOf(),
) {

    override fun toString(): String {
        return "ID = $id, Title = $title"
    }

    fun chooseRandomEventPhoto(): Photo? {
        if (photos.isEmpty()) {
            return null
        }
        val randomIndex = Random().nextInt(photos.size)
        return photos[randomIndex]
    }
}