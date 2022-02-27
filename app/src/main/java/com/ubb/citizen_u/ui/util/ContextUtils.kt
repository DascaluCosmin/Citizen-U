package com.ubb.citizen_u.ui.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.ubb.citizen_u.util.SettingsConstants

fun Context.getCurrentLanguage(): String {
    val settingsPreferences = PreferenceManager
        .getDefaultSharedPreferences(applicationContext)
    return settingsPreferences.getString(
        SettingsConstants.LANGUAGE_SETTINGS_KEY, SettingsConstants.DEFAULT_LANGUAGE
    ) ?: SettingsConstants.DEFAULT_LANGUAGE
}