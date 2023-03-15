package com.call.colorscreen.ledflash.receive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.call.colorscreen.ledflash.service.ColorCallService
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.HawkData
import com.call.colorscreen.ledflash.util.PhoneUtils


class PhoneReceiver : BroadcastReceiver(), PhoneUtils.PhoneListener {
    var phoneNumber: String? = null
    val TYPE_END_CALL = 0
    val TYPE_IN_CALL = 2
    val TYPE_RINGGING_CALL = 1
    var intentCallService: Intent? = null
    var isBiggerAndroidP = false
    var stateType = 0
    var context: Context? = null
    var isFirstRun = false
    override fun onReceive(context: Context?, intent: Intent) {
        this.context = context
        if (intent.extras != null) {
            val state = intent.extras!!.getString("state")
            phoneNumber = intent.extras!!.getString("incoming_number")
            if (phoneNumber == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                isBiggerAndroidP = true
                PhoneUtils.get(context).getNumberPhoneWhenNull(this@PhoneReceiver)
            }
            if (state != null && state == TelephonyManager.EXTRA_STATE_IDLE) {
                stateType = TYPE_END_CALL
            } else if (state != null && state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                stateType = TYPE_IN_CALL
            } else if (state != null && state == TelephonyManager.EXTRA_STATE_RINGING) {
                stateType = TYPE_RINGGING_CALL
            }
            onCallStateChanged(context, stateType)
        }
    }

    private fun onCallStateChanged(context: Context?, state: Int) {
        when (state) {
            TYPE_RINGGING_CALL -> {
                if (phoneNumber != null) {
                    onIncommingCall(context, phoneNumber!!)
                }
            }
            TYPE_END_CALL, TYPE_IN_CALL -> finishCall()
        }
       /* if (flashUtils != null && flashUtils.isRunning()) {
            flashUtils.stop()
        }*/
    }

    private fun onIncommingCall(context: Context?, number: String) {
        if (AppUtil.checkDrawOverlay(context) && HawkData.getEnableCall()) {
            intentCallService = Intent(context, ColorCallService::class.java)
            intentCallService!!.putExtra(Constant.PHONE_NUMBER, number)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(intentCallService)
            } else {
                context?.startService(intentCallService)
            }
        }
    }

    fun finishCall() {
        val localBroadcastManager = context?.let {
            LocalBroadcastManager
                .getInstance(it)
        }
        localBroadcastManager?.sendBroadcast(Intent("com.callcolor.endCall"))
    }

    override fun getNumPhone(phoneNumb: String?) {
        if (!isFirstRun) {
            phoneNumber = phoneNumb
            isFirstRun = true
            isBiggerAndroidP = false
            onCallStateChanged(context, stateType)
        }
    }

    companion object {
       // var flashUtils: FlashUtils? = null
    }
}