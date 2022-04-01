package com.ubb.citizen_u.util.glide

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ubb.citizen_u.data.model.Photo

object ImageFiller {

    fun fill(context: Context, imageView: ImageView, photo: Photo?) {
        if (photo == null) {
            return
        }
        val storageReference = photo.storageReference

        Glide.with(context)
            .load(storageReference)
            .into(imageView)
    }

    fun fill(context: Context, imageView: ImageView, uri: Uri?) {
        if (uri == null) {
            return
        }
        Glide.with(context)
            .load(uri)
            .into(imageView)
    }
}