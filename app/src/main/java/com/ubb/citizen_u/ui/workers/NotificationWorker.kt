package com.ubb.citizen_u.ui.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ubb.citizen_u.R
import com.ubb.citizen_u.data.model.events.PeriodicEvent
import com.ubb.citizen_u.data.model.events.PeriodicEventFrequency
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.events.EventUseCases
import com.ubb.citizen_u.ui.AuthenticationActivity
import com.ubb.citizen_u.ui.util.getCurrentLanguage
import com.ubb.citizen_u.ui.util.getDay
import com.ubb.citizen_u.ui.util.loadLocale
import com.ubb.citizen_u.util.NotificationsConstants
import com.ubb.citizen_u.util.SettingsConstants.DEFAULT_LANGUAGE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import java.util.*

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val eventUseCases: EventUseCases,
) : CoroutineWorker(context, parameters) {

    companion object {
        private const val TAG = "UBB-NotificationWorker"
    }

    override suspend fun doWork(): Result {
        eventUseCases.getAllPeriodicEventsUseCase().collect {
            when (it) {
                Response.Loading -> {
                    Log.d(TAG, "collectGetAllPeriodicEventsState: Collecting response $it")
                }
                is Response.Error -> {
                    Log.e(TAG,
                        "collectGetAllPeriodicEventsState: Error at collecting the periodic events: ${it.message}")
                }
                is Response.Success -> {
                    Log.d(TAG,
                        "collectGetAllPeriodicEventsState: Successfully collected ${it.data.size} periodic events")
                    it.data
                        .filter(this::shouldEmitPeriodicEventNotification)
                        .forEach(this::emitPeriodicEventNotification)
                }
            }
        }
        return Result.success()
    }

    private fun shouldEmitPeriodicEventNotification(periodicEvent: PeriodicEvent?): Boolean {
        val now = Calendar.getInstance()
        return when (periodicEvent?.frequency) {
            PeriodicEventFrequency.WEEKLY -> {
                now.getDay() == periodicEvent.happeningDay.toString()
            }
            PeriodicEventFrequency.MONTHLY -> {
                false
            }
            PeriodicEventFrequency.ANNUALLY -> {
                false
            }
            null -> false
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun emitPeriodicEventNotification(periodicEvent: PeriodicEvent?) {
        Log.d(TAG,
            "emitPeriodicEventNotification: Emitting notification for periodic event ${
                periodicEvent?.title?.get(DEFAULT_LANGUAGE)
            }")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationsConstants.CHANNEL_ID,
                NotificationsConstants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NotificationsConstants.CHANNEL_DESCRIPTION
            }

            val manager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, AuthenticationActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val eventTitle = periodicEvent?.title?.get(applicationContext.getCurrentLanguage())
            ?: applicationContext.getString(R.string.periodic_event_generic_title)
        val notification = NotificationCompat.Builder(applicationContext,
            NotificationsConstants.CHANNEL_ID)
            .setContentTitle(applicationContext.getString(R.string.city_hall_name))
            .setContentText(eventTitle)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.town_hall_ic)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(NotificationsConstants.NOTIFICATION_ID, notification)
    }
}