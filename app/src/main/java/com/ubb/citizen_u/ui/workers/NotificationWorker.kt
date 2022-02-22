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
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ubb.citizen_u.R
import com.ubb.citizen_u.ui.AuthenticationActivity
import com.ubb.citizen_u.util.NotificationsConstants

class NotificationWorker(
    context: Context,
    parameters: WorkerParameters,
) : Worker(context, parameters) {

    @SuppressLint("ObsoleteSdkInt")
    override fun doWork(): Result {
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

        val notification = NotificationCompat.Builder(applicationContext,
            NotificationsConstants.CHANNEL_ID)
            .setContentTitle(applicationContext.getString(R.string.city_hall_name))
            .setContentText("Test Text")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.logo_bg_free)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(NotificationsConstants.NOTIFICATION_ID, notification)

        return Result.success()
    }
}