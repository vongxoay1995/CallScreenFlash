package com.call.colorscreen.ledflash.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.call.colorscreen.ledflash.call.IncommingCallActivity

class NotifiUtil {

    fun NotifiUtil() {}
    companion object {
        private val ID_NOTIFICATION = 1
        var CHANNEL = "Color_Call_channel"
        private val CHANNEL_ID = "ColorCall"
        @JvmStatic
        @RequiresApi(api = Build.VERSION_CODES.Q)
        fun initNotificationAndroidQ(context: Context): Notification? {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            val fullScreenIntent = Intent(context, IncommingCallActivity::class.java)
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL) // Use a full-screen intent only for the highest-priority alerts where you
                // have an associated activity that you would like to launch after the user
                // interacts with the notification. Also, if your app targets Android 10
                // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                // order for the platform to invoke this notification.
                .setFullScreenIntent(fullScreenPendingIntent, true)
            return notificationBuilder.build()
        }
        @JvmStatic
        @RequiresApi(api = Build.VERSION_CODES.O)
        fun initNotificationAndroidO(context: Context): Notification? {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            return NotificationCompat.Builder(context, CHANNEL_ID)
                .build()
        }
        @JvmStatic
        fun hideNotification(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.cancel(ID_NOTIFICATION)
        }
    }


}