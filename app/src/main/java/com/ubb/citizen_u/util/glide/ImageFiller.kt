package com.ubb.citizen_u.util.glide

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ubb.citizen_u.data.model.events.EventPhoto

object ImageFiller {

    fun fill(context: Context, imageView: ImageView, eventPhoto: EventPhoto) {
        val storageReference = eventPhoto.storageReference

        Glide.with(context)
            .load(storageReference)
            .into(imageView)
    }
}