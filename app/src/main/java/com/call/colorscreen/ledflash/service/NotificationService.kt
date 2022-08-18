package com.call.colorscreen.ledflash.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
    }

    override fun getActiveNotifications(): Array<StatusBarNotification?> {
        var statusBarNotificationArr: Array<StatusBarNotification?>? = null
        try {
            statusBarNotificationArr = super.getActiveNotifications()
        } catch (e: SecurityException) {
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
        return statusBarNotificationArr ?: arrayOfNulls(0)
    }
}