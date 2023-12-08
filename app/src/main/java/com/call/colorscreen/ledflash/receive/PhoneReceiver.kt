package com.call.colorscreen.ledflash.receive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.call.colorscreen.ledflash.call.IncommingCallActivity
import com.call.colorscreen.ledflash.service.ColorCallService
import com.call.colorscreen.ledflash.service.NotificationService
import com.call.colorscreen.ledflash.service.PhoneStateService
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.FlashlightProvider
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
    lateinit var flashlightProvider: FlashlightProvider

    override fun onReceive(context: Context?, intent: Intent) {
        Log.e("TAN", "onReceive: action")
        this.context = context
        Thread {
            Log.e("TAN", "PhoneReceiver: ")
            if (intent.extras != null) {
                val state = intent.extras!!.getString("state")
                phoneNumber = intent.extras!!.getString("incoming_number")
                Log.e("TAN", "onReceive: 1" + phoneNumber)
                if (phoneNumber == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    isBiggerAndroidP = true
                    Log.e("TAN", "onReceive: 2")
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
                flashlightProvider = FlashlightProvider.getInstance(context!!)
                flashlightProvider.let {
                    val isAllow = HawkData.getEnableFlash()
                    if (isAllow) {
                        flashlightProvider.releaseCamera()

                        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                                .equals(TelephonyManager.EXTRA_STATE_RINGING)
                        ) {
                            flashlightProvider.isPause = false
                            flashlightProvider.isInterruptThread = false
                            flashlightProvider.flashCall()
                        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                                .equals(TelephonyManager.EXTRA_STATE_OFFHOOK)
                        ) {
                            flashlightProvider.turnOffFlash()
                            flashlightProvider.isPause = true
                            flashlightProvider.flashCall()
                            flashlightProvider.releaseCamera()
                        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                                .equals(TelephonyManager.EXTRA_STATE_IDLE)
                        ) {
                            flashlightProvider.turnOffFlash()
                            flashlightProvider.isPause = true
                            flashlightProvider.flashCall()
                            flashlightProvider.releaseCamera()
                        }
                    }
                }
            }
        }.start()

    }

    private fun onCallStateChanged(context: Context?, state: Int) {
        Log.e("TAN", "onCallStateChanged: 00", )
        when (state) {
            TYPE_RINGGING_CALL -> {
                Log.e("TAN", "onCallStateChanged: 111"+phoneNumber )
                if (phoneNumber != null) {
                    onIncommingCall(context, phoneNumber!!)
                }else {
                    if (NotificationService.get()==null){
                        onIncommingCall(context,"")
                    }
                }
            }

            TYPE_END_CALL, TYPE_IN_CALL -> finishCall()
        }
        /* if (flashUtils != null && flashUtils.isRunning()) {
             flashUtils.stop()
         }*/
    }

    private fun onIncommingCall(context: Context?, number: String) {
        Log.e("TAN", "onIncommingCall receiver: "+number)
        if (AppUtil.checkDrawOverlayAppNew(context) && HawkData.getEnableCall()) {
            intentCallService = Intent(context, ColorCallService::class.java)
            intentCallService!!.putExtra(Constant.PHONE_NUMBER, number)
            Log.e("TAN", "onIncommingCall: 11111"+intentCallService )
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
        //localBroadcastManager?.sendBroadcast(Intent("com.callcolor.endCall"))
        context?.stopService( Intent(
            context,
            ColorCallService::class.java
        ))
    }

    override fun getNumPhone(phoneNumb: String?) {
        Log.e("TAN", "getNumPhone receiver: "+phoneNumb)
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