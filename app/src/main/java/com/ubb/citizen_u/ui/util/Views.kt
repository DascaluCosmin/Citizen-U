package com.ubb.citizen_u.ui.util

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN
import com.ubb.citizen_u.util.SettingsConstants
import java.util.*

fun Fragment.toastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastErrorMessage(errorMessage: String = DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN) {
    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
}

@Suppress("DEPRECATION")
fun Fragment.loadLocale() {
    val settingsPreferences = PreferenceManager
        .getDefaultSharedPreferences(requireContext())
    val language = settingsPreferences
        .getString(SettingsConstants.LANGUAGE_SETTINGS_KEY,
            SettingsConstants.DEFAULT_LANGUAGE) ?: SettingsConstants.DEFAULT_LANGUAGE


    val locale = Locale(language)
    val configuration = Configuration()

    configuration.setLocale(locale)
    resources.updateConfiguration(
        configuration,
        resources.displayMetrics
    )
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