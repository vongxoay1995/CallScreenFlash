package com.call.colorscreen.ledflash.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.receive.PhoneStateListenerHighAPI
import com.call.colorscreen.ledflash.receive.PhoneStateListenerLowAPI
import com.call.colorscreen.ledflash.ui.main.MainActivity
import org.koin.android.ext.android.inject

class PhoneStateService : Service() {
    var telephony: TelephonyManager? = null
    var phoneStateListener: PhoneStateListener? = null
    var callStateListener: TelephonyCallback? = null
    val database by inject<AppDatabase>()
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                NotificationChannel(
                    "channel_call",
                    "notification",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
        val builder = NotificationCompat.Builder(this, "channel_call")
        builder.setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        builder.setContentTitle(getString(R.string.app_name))
        builder.setContentText(getString(R.string.notify_msg_foreground))
        builder.setSmallIcon(R.drawable.icon_app)
        startForeground(1, builder.build())
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        telephony = telephonyManager
        registerCallStateListener(telephonyManager)
    }

    private fun registerCallStateListener(telephonyManager: TelephonyManager) {
        if (!callStateListenerRegistered) {
            Log.e("TAN", "registerCallStateListener: ")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.e("TAN", "registerCallStateListener: 0")
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e("TAN", "registerCallStateListener: 1")
                    callStateListener = PhoneStateListenerHighAPI(this,database)
                    telephonyManager.registerTelephonyCallback(
                        mainExecutor,
                        callStateListener!!
                    )
                    callStateListenerRegistered = true
                }
            } else {
                Log.e("TAN", "registerCallStateListener: 2222")
                phoneStateListener = PhoneStateListenerLowAPI(this,database)
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
                callStateListenerRegistered = true
            }
        }
    }

    private var callStateListenerRegistered = false
    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephony!!.unregisterTelephonyCallback(callStateListener!!)
        } else {
            telephony!!.listen(phoneStateListener, 0)
        }
        telephony = null
        phoneStateListener = null
    }

    override fun onStartCommand(intent: Intent?, i: Int, i2: Int): Int {
        Log.e("TAN", "onStartCommand: "+intent)
       /* if(intent != null && intent.getAction() != null) {
            return START_STICKY
        }else{

        }

        if (null == intent || null == intent.action) {

        }*/
        return START_STICKY
    }

    companion object {
        var number: String? = null
        fun startService(context: Context?) {
            ContextCompat.startForegroundService(
                context!!,
                Intent(context, PhoneStateService::class.java)
            )
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, PhoneStateService::class.java))
        }

        fun setNumberPhone(numberPhone: String?) {
            number = numberPhone
        }
    }
}
