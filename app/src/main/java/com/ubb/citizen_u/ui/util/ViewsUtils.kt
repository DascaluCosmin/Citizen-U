package com.ubb.citizen_u.ui.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.View
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ubb.citizen_u.R
import com.ubb.citizen_u.util.DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN
import com.ubb.citizen_u.util.SettingsConstants
import com.ubb.citizen_u.util.SettingsConstants.DEFAULT_LANGUAGE
import java.util.*

fun Fragment.toastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toastErrorMessage(errorMessage: String = DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN) {
    if (errorMessage == DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN) {
        Toast.makeText(context,
            getString(R.string.DEFAULT_ERROR_MESSAGE_PLEASE_TRY_AGAIN),
            Toast.LENGTH_SHORT).show()
    }
    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
}

@Suppress("DEPRECATION")
fun Fragment.loadLocale() {
    val language = getPreferenceLanguage()
    changeLocale(language)
}

fun Fragment.getDefaultLocalizedStringResource(@StringRes resourceId: Int): String {
    val preferenceLanguage = getPreferenceLanguage()

    changeLocale(DEFAULT_LANGUAGE)
    val resource = resources.getString(resourceId)
    changeLocale(preferenceLanguage)

    return resource
}

fun Fragment.getDefaultLocalizedArrayStringResource(@ArrayRes resourceId: Int): Array<String> {
    val preferenceLanguage = getPreferenceLanguage()

    changeLocale(DEFAULT_LANGUAGE)
    val resource = resources.getStringArray(resourceId)
    changeLocale(preferenceLanguage)

    return resource
}

private fun Fragment.changeLocale(language: String) {
    val locale = Locale(language)
    val configuration = Configuration()

    configuration.setLocale(locale)
    resources.updateConfiguration(configuration, resources.displayMetrics)
}

private fun Fragment.getPreferenceLanguage(): String {
    val settingsPreferences = PreferenceManager
        .getDefaultSharedPreferences(requireContext())
    return settingsPreferences
        .getString(SettingsConstants.LANGUAGE_SETTINGS_KEY,
            SettingsConstants.DEFAULT_LANGUAGE) ?: SettingsConstants.DEFAULT_LANGUAGE
}

@Suppress("DEPRECATION")
fun Activity.loadLocale() {
    val settingsPreferences = PreferenceManager
        .getDefaultSharedPreferences(this)
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

fun View.fadeOut() {
    animate()
        .alpha(0.0f)
        .setDuration(1000L)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.GONE
            }
        })
}

fun View.fadeIn() {
    apply {
        alpha = 0.0f
        visibility = View.VISIBLE
        animate()
            .alpha(1.0f)
            .setDuration(2000L)
            .setListener(null)
    }
}

fun Context.getStringResourceLocalized(locale: Locale?, resourceId: Int): String {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration).getText(resourceId).toString()
}