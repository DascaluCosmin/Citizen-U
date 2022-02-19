package com.ubb.citizen_u.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.ubb.citizen_u.R
import com.ubb.citizen_u.ui.util.loadLocale

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadLocale()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}