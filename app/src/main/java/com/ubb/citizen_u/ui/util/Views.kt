package com.ubb.citizen_u.ui.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN

fun Fragment.toastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastErrorMessage(errorMessage: String = DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN) {
    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
}

fun getRotatedBitmap(path: String?, sourceBitmap: Bitmap?): Bitmap? {
    if (path == null || sourceBitmap == null) {
        return sourceBitmap
    }
    val exif = ExifInterface(path)

    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90.0f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180.0f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270.0f)
        else -> matrix.postRotate(0.0f)
    }

    return Bitmap.createBitmap(sourceBitmap,
        0,
        0,
        sourceBitmap.width,
        sourceBitmap.height,
        matrix,
        true)
}