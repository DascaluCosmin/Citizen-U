package com.ubb.citizen_u.ui.fragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.ubb.citizen_u.R
import com.ubb.citizen_u.util.SettingsConstants.LANGUAGE_SETTINGS_KEY

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val preference = findPreference<ListPreference>(LANGUAGE_SETTINGS_KEY)
        preference?.setOnPreferenceChangeListener { _, _ ->
            requireActivity().run {
                // Restart activity in order to set locale
                finish()
                startActivity(intent)
            }
            true
        }
    }
}