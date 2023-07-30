package com.call.colorscreen.ledflash.receive

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.util.Log
import android.view.View
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.ui.main.PermissionOverActivity
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.PhoneUtils
import com.call.colorscreen.ledflash.view.RingingCallView

class PhoneStateListenerLowAPI(var context: Context,var database: AppDatabase) : PhoneStateListener(),
    PhoneUtils.PhoneListener {
    var handler = Handler()
    var audio: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    var ringerMode = -1
    var incomingCallView: RingingCallView? = null
    var isFirstRun = false
    fun grantPermissionActivity() {
        val intent = Intent(context, PermissionOverActivity::class.java)
        intent.putExtra(Constant.TYPE_PERMISSION, 1)
        context.startActivity(intent)
    }

    fun setRinger(mode: Int) {
        try {
            audio.ringerMode = mode
        } catch (unused: SecurityException) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= 24 && notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted) {
                context.startActivity(Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"))
                handler.postDelayed({ grantPermissionActivity() }, 100)
            }
        }
    }

    fun release() {
        if (incomingCallView != null) {
            val i = ringerMode
            if (i != -1) {
                setRinger(i)
                ringerMode = -1
            }
            incomingCallView!!.release()
            incomingCallView = null
        }
    }

    override fun onCallStateChanged(i: Int, str: String) {
        super.onCallStateChanged(i, str)
        state = i
        showViewCall(str)
        isFirstRun = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.e("TAN", "onCallStateChanged: getNumberPhoneWhenNull")
            PhoneUtils.get(context).getNumberPhoneWhenNull(this@PhoneStateListenerLowAPI)
        }
    }

    var state = 0

    override fun getNumPhone(str: String?) {
        if (!isFirstRun) {
            isFirstRun = true
            Handler(Looper.getMainLooper()).post {
                if (incomingCallView != null) {
                    incomingCallView!!.setNumberPhone(str)
                }
            }
        }
    }

    fun showViewCall(str: String?) {
        Log.e("TAN", "showViewCall: $state")
        if (state != 0) {
            if (state == 1) {
                incomingCallView =
                    View.inflate(context, R.layout.layout_ringing_call, null) as RingingCallView
                incomingCallView!!.phoneStateLowAPI = this
                incomingCallView!!.setDatabaseApp(database)
                incomingCallView!!.initData()
                incomingCallView!!.setNumberPhone(str)
                if (ringerMode == -1) {
                    ringerMode = audio.ringerMode
                    setRinger(1)
                    return
                }
                return
            } else if (state != 2) {
                return
            }
        }
        release()
        // }
    }
}
