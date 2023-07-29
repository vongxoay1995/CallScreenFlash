package com.call.colorscreen.ledflash.receive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.call.colorscreen.ledflash.service.PhoneStateService
import com.call.colorscreen.ledflash.util.HawkData

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action && HawkData.getEnableCall()) {
            PhoneStateService.startService(context)
        }
    }
}