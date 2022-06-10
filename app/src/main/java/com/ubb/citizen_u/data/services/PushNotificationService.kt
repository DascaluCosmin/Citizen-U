package com.ubb.citizen_u.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ubb.citizen_u.R
import com.ubb.citizen_u.util.NotificationsConstants.EVENT_PUSH_NOTIFICATION_ID

class PushNotificationService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "EVENT_NOTIFICATIONS_CHANNEL_ID"
        private const val CHANNEL_NAME = "EventNotificationsChannel"
    }

    @Override
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
        val body = message.notification?.body

        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.town_hall_ic)
            .setAutoCancel(true)
        NotificationManagerCompat.from(this)
            .notify(EVENT_PUSH_NOTIFICATION_ID, notification.build())

    }
}