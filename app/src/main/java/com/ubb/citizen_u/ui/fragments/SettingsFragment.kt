package com.ubb.citizen_u.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ubb.citizen_u.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}