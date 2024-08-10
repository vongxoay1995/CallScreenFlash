package com.call.colorscreen.ledflash.util

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Message
import android.text.TextUtils

class NetworkChangeReceiver : BroadcastReceiver() {
    private var listener: Listener? = null

    fun registerReceiver(context: Context?, listener: Listener?) {
        if (context == null) {
            return
        }
        this.listener = listener
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            context.registerReceiver(this, filter,RECEIVER_NOT_EXPORTED)
        }else{
            context.registerReceiver(this, filter)
        }
    }

    fun unregisterReceiver(context: Context?) {
        if (context == null) {
            return
        }
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context?, intent: Intent) {
        val t: Thread = object : Thread() {
            override fun run() {
                val message = Message()
                val action = intent.action
                message.obj = context
                if (listener != null && (if (TextUtils.isEmpty(action)) "" else action) == ConnectivityManager.CONNECTIVITY_ACTION) {
                    message.what = 1
                }
                handler.sendMessage(message)
            }
        }
        t.start()
    }

    @SuppressLint("HandlerLeak")
    val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val context = msg.obj as Context
            if (msg.what == 1) {
                listener!!.netWorkStateChanged(AppUtil.checkInternet(context))
            }
            super.handleMessage(msg)
        }
    }

    interface Listener {
        fun netWorkStateChanged(isNetWork: Boolean)
    }
}