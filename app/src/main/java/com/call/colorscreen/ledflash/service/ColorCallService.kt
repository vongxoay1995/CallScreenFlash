package com.call.colorscreen.ledflash.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.internal.telephony.ITelephony
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.call.IncommingCallActivity
import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.database.Contact
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.receive.PhoneStateListenerHighAPI
import com.call.colorscreen.ledflash.receive.PhoneStateListenerLowAPI
import com.call.colorscreen.ledflash.ui.main.MainActivity
import com.call.colorscreen.ledflash.ui.main.PermissionOverActivity
import com.call.colorscreen.ledflash.util.*
import com.call.colorscreen.ledflash.view.RingingCallView
import com.call.colorscreen.ledflash.view.TextureVideoView
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import org.koin.android.ext.android.inject

class ColorCallService : Service() {
    private var phone: String = ""
    private lateinit var themeSelect: Theme
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var telephonyService: ITelephony
    private val ID_NOTIFICATION = 1
    var isDisable = false
    private var mLocalBroadcastManager: LocalBroadcastManager? = null

    //var telephony: TelephonyManager? = null
    ///private var callStateListenerRegistered = false
    // var phoneStateListener: PhoneStateListener? = null
    //var callStateListener: TelephonyCallback? = null
    val database by inject<AppDatabase>()
    var incomingCallView: RingingCallView? = null
    var audio: AudioManager? = null
    lateinit var context: Context
    var handler = Handler()
    var ringerMode = -1


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (intent != null && intent.extras != null) {

                phone = intent.getStringExtra(Constant.PHONE_NUMBER).toString()
                phone = phone.replace(" ".toRegex(), "").replace("-".toRegex(), "")
            }
            showViewCall(phone)
        } catch (e: Exception) {
            e.message
        }
        return START_NOT_STICKY
        //return super.onStartCommand(intent, flags, startId)
    }

    /*   private fun registerCallStateListener(telephonyManager: TelephonyManager) {
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
                       callStateListener = PhoneStateListenerHighAPI(this, database)
                       telephonyManager.registerTelephonyCallback(
                           mainExecutor,
                           callStateListener!!
                       )
                       callStateListenerRegistered = true
                   }
               } else {
                   Log.e("TAN", "registerCallStateListener: 2222")
                   phoneStateListener = PhoneStateListenerLowAPI(this, database)
                   telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
                   callStateListenerRegistered = true
               }
           }
       }*/



    override fun onDestroy() {
        mLocalBroadcastManager!!.unregisterReceiver(mBroadcastReceiver)
        if (incomingCallView != null && incomingCallView?.windowManager != null) {
            Log.e("TAN", "onDestroy: ", )
            incomingCallView?.release()
        }
        // NotifiUtil.hideNotification(this)
        super.onDestroy()
        Log.e("TAN", "onDestroy: service"+this )
        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
             telephony!!.unregisterTelephonyCallback(callStateListener!!)
         } else {
             telephony!!.listen(phoneStateListener, 0)
         }
         telephony = null
         phoneStateListener = null*/
    }

    override fun onCreate() {
        Log.e("TAN", "onCreate: color call service"+this)
        // analystic = Analystic.getInstance(this)
        //analystic.trackEvent(ManagerEvent.callServiceOncreate())
        context = this

        audio = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)
        val mIntentFilter = IntentFilter()
        mIntentFilter.addAction("com.callcolor.endCall")
        mLocalBroadcastManager!!.registerReceiver(mBroadcastReceiver, mIntentFilter)



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
//        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//        telephony = telephonyManager
        //registerCallStateListener(telephonyManager)
        super.onCreate()
    }

    var mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val t: Thread = object : Thread() {
                override fun run() {
                    if (intent.action == "com.callcolor.endCall") {
                       // stopCallService()
                    }
                }
            }
            t.start()
        }
    }

    fun stopCallService() {
        applicationContext.stopService(
            Intent(
                applicationContext,
                ColorCallService::class.java
            )
        )
    }

    fun showViewCall(str: String?) {
        Log.e("TAN", "showViewCall: ")
        incomingCallView =
            View.inflate(context, R.layout.layout_ringing_call, null) as RingingCallView
        //incomingCallView!!.phoneStateLowAPI = this
        incomingCallView!!.setDatabaseApp(database)
        incomingCallView!!.initData()
        incomingCallView!!.setNumberPhone(str)
        if (ringerMode == -1) {
            ringerMode = audio!!.ringerMode
            setRinger(1)
        }
        // }
    }

    fun grantPermissionActivity() {
        val intent = Intent(context, PermissionOverActivity::class.java)
        intent.putExtra(Constant.TYPE_PERMISSION, 1)
        context.startActivity(intent)
    }

    fun setRinger(mode: Int) {
        try {
            audio?.ringerMode = mode
        } catch (unused: SecurityException) {
            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= 24 && !notificationManager.isNotificationPolicyAccessGranted) {
                this.startActivity(Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"))
                handler.postDelayed({ grantPermissionActivity() }, 100)
            }
        }
    }

    fun release() {
        if (incomingCallView != null) {
            if (ringerMode != -1) {
                setRinger(ringerMode)
                ringerMode = -1
            }
            incomingCallView!!.release()
            incomingCallView = null
        }
    }
}