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
import com.call.colorscreen.ledflash.util.PhoneUtils

class PhoneStateListenerLowAPI(var context: Context) : PhoneStateListener(),
    PhoneUtils.PhoneListener {
    var handler = Handler()
    var audio: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    var ringerMode = -1
    var incomingCallView: IncomingCallView? = null
    var isFirstRun = false
    fun grantPermissionActivity() {
        val intent = Intent(context, PermissionOverLayActivity::class.java)
        intent.putExtra(TYPE_PROMPT, 1)
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
            incomingCallView.release()
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

    fun getNumPhone(str: String?) {
        if (!isFirstRun) {
            isFirstRun = true
            Handler(Looper.getMainLooper()).post {
                if (incomingCallView != null) {
                    incomingCallView.setNumberPhone(str)
                }
            }
        }
    }

    fun showViewCall(str: String?) {
        Log.e("TAN", "showViewCall: $state")
        if (state != 0) {
            if (state == 1) {
                incomingCallView =
                    View.inflate(context, R.layout.layout_call_color, null) as IncomingCallView
                incomingCallView.phoneState = this
                incomingCallView.initData()
                incomingCallView.setNumberPhone(str)
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
