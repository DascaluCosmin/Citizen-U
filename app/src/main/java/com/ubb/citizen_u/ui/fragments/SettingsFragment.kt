package com.ubb.citizen_u.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.ubb.citizen_u.R
import com.ubb.citizen_u.ui.util.toastMessage
import com.ubb.citizen_u.ui.workers.NotificationWorker
import com.ubb.citizen_u.util.NotificationsConstants.NOTIFICATION_WORKER_TAG
import com.ubb.citizen_u.util.SettingsConstants.LANGUAGE_SETTINGS_KEY
import com.ubb.citizen_u.util.SettingsConstants.NOTIFICATIONS_SETTINGS_KEY
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        private const val TAG = "UBB-SettingsFragment"
        private const val DAILY_NOTIFICATION_HOUR = 6
        private const val REPEAT_INTERVAL_VALUE_UNIT = 1L
    }

    private lateinit var workManager: WorkManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        workManager = WorkManager.getInstance(requireContext())

        val languagePreference = findPreference<ListPreference>(LANGUAGE_SETTINGS_KEY)
        languagePreference?.setOnPreferenceChangeListener { _, _ ->
            requireActivity().run {
                // Restart activity in order to set locale
                finish()
                startActivity(intent)
            }
            true
        }

        val notificationsPreference = findPreference<SwitchPreference>(NOTIFICATIONS_SETTINGS_KEY)
        notificationsPreference?.setOnPreferenceClickListener { _ ->
            if (notificationsPreference.isChecked) {
                Log.d(TAG, "onCreatePreferences: Enabling notifications...")
                toastMessage(getString(R.string.settings_notification_summary_on_text))
                createNotificationWork()
            } else {
                Log.d(TAG, "onCreatePreferences: Disabling notifications...")
                toastMessage(getString(R.string.settings_notifications_summary_off_text))
                cancelNotificationWork()
            }
            true
        }
    }

    private fun createNotificationWork() {
        val delay = computeDelayInMinutes()
        Log.d(TAG,
            "createNotificationWork: Notification Work will be delayed and executed in approximately $delay minutes")
        val notificationWork = PeriodicWorkRequest.Builder(
            NotificationWorker::class.java,
            REPEAT_INTERVAL_VALUE_UNIT,
            TimeUnit.HOURS
        )
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            NOTIFICATION_WORKER_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWork
        )
    }

    private fun computeDelayInMinutes(): Long {
        val dailyNotificationHourCalendar = Calendar.getInstance()
        dailyNotificationHourCalendar.set(Calendar.HOUR_OF_DAY, DAILY_NOTIFICATION_HOUR)
        dailyNotificationHourCalendar.set(Calendar.MINUTE, 0)
        dailyNotificationHourCalendar.set(Calendar.SECOND, 0)

        val now = Calendar.getInstance()
        val delta = (dailyNotificationHourCalendar.timeInMillis - now.timeInMillis)
            .milliseconds
            .inWholeMinutes
        return if (delta > 0) delta else delta + TimeUnit.DAYS.toMinutes(1)
    }

    private fun cancelNotificationWork() {
        workManager.cancelUniqueWork(NOTIFICATION_WORKER_TAG)
    }
}