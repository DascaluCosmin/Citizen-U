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
import com.ubb.citizen_u.ui.util.getDayOfWeek
import com.ubb.citizen_u.ui.util.getMonth
import com.ubb.citizen_u.util.CalendarConstants
import com.ubb.citizen_u.util.NotificationsConstants
import com.ubb.citizen_u.util.NotificationsConstants.NOTIFICATION_PERIODIC_EVENT_ID_KEY
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
                    Log.d(TAG, "doWork: Collecting response $it")
                }
                is Response.Error -> {
                    Log.e(TAG,
                        "doWork: Error at collecting the periodic events: ${it.message}")
                }
                is Response.Success -> {
                    Log.d(TAG,
                        "doWork: Successfully collected ${it.data.size} periodic events")

                    var numberOfEmittedEvents = 0
                    it.data
                        .filterNotNull()
                        .filter(this::shouldEmitPeriodicEventNotification)
                        .forEach { periodicEvent ->
                            numberOfEmittedEvents++
                            emitPeriodicEventNotification(periodicEvent)
                        }
                    Log.i(TAG,
                        "doWork: Today $numberOfEmittedEvents periodic event notifications have been emitted")
                }
            }
        }
        return Result.success()
    }

    private fun shouldEmitPeriodicEventNotification(periodicEvent: PeriodicEvent): Boolean {
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_MONTH)
        val notificationDaysForMonthFrequency = listOf(1, 21)
        val notificationDaysForAnnuallyFrequency = listOf(1, 15, 27)

        return when (periodicEvent.frequency) {
            PeriodicEventFrequency.WEEKLY -> {
                now.getDayOfWeek() == periodicEvent.happeningDay.toString()
            }
            PeriodicEventFrequency.MONTHLY -> {
                now.getMonth() == periodicEvent.happeningMonth.toString() &&
                        currentDay in notificationDaysForMonthFrequency
            }
            PeriodicEventFrequency.ANNUALLY -> {
                now.getMonth() == CalendarConstants.LAST_MONTH_OF_YEAR &&
                        currentDay in notificationDaysForAnnuallyFrequency
            }
            null -> false
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun emitPeriodicEventNotification(periodicEvent: PeriodicEvent) {
        Log.d(TAG,
            "emitPeriodicEventNotification: Emitting notification for periodic event ${
                periodicEvent.title[DEFAULT_LANGUAGE]
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
        intent.putExtra(NOTIFICATION_PERIODIC_EVENT_ID_KEY, periodicEvent.id)

        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val eventTitle = periodicEvent.title[applicationContext.getCurrentLanguage()]
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