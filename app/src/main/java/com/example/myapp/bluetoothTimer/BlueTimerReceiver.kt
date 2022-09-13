package com.example.myapp.bluetoothTimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapp.NOTIFICATION_CHANNEL_ID

class BlueTimerReceiver : BroadcastReceiver() {

    private val LOG_TAG = "myLogs"

    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onReceive(context: Context, intent: Intent?) {
        notificationManagerCompat = NotificationManagerCompat.from(context)

        sendNotification(1, context)
    }

    fun sendNotification(id: Int, context: Context) {
        notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("Будильник")
            .setContentText("Сработал")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        with(notificationManagerCompat) {
            notify(id, notificationBuilder.build())
        }
    }
}