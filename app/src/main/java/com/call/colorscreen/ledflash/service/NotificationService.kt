package com.call.colorscreen.ledflash.service

import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import com.call.colorscreen.ledflash.util.PhoneUtils
import java.lang.ref.WeakReference


class NotificationService : NotificationListenerService() {

    private var inCallNotification: StatusBarNotification? = null
    private var isListen = false

    fun getInCallNotification(): StatusBarNotification? {
        return inCallNotification
    }
    companion object{
        private var serviceWeakReference: WeakReference<NotificationService?>? = null
        fun get(): NotificationService? {
            val weakReference = serviceWeakReference
            return if (weakReference?.get() == null) {
                null
            } else serviceWeakReference!!.get()
        }
    }

    override fun onCreate() {
        super.onCreate()
        serviceWeakReference = WeakReference(this)
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification) {
        super.onNotificationPosted(statusBarNotification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!statusBarNotification.packageName.contains("incallui") && !statusBarNotification.packageName.contains(
                    "dialer"
                )
            ) return
            var str = ""
            val telecomManager = applicationContext.getSystemService(
                TelecomManager::class.java
            )
            if (telecomManager != null) {
                str = telecomManager.defaultDialerPackage + ""
            }
            if (!statusBarNotification.packageName.contains("incallui") && statusBarNotification.packageName != str) {
                return
            }
            if (isListen) {
                object : Thread() {
                    override fun run() {
                        super.run()
                        PhoneUtils.get(applicationContext)?.getPhoneFromNotificationListen(statusBarNotification)
                    }
                }.start()
                return
            }
            inCallNotification = statusBarNotification
        }
    }

    fun stopListenColorCall() {
        applicationContext.stopService(Intent(applicationContext, NotificationService::class.java))
        isListen = false
    }

    @RequiresApi(api = 28)
    fun startListenColorCall() {
        isListen = true
    }
}