package com.call.colorscreen.ledflash.service

import android.Manifest
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.internal.telephony.ITelephony
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.call.IncommingCallActivity
import com.call.colorscreen.ledflash.database.Contact
import com.call.colorscreen.ledflash.database.RoomManager
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.ui.main.custom.TextureVideoView
import com.call.colorscreen.ledflash.util.*
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView

class ColorCallService:LifecycleService() {
    private var phone: String = ""
    private lateinit var viewCall: View
    private lateinit var vdoBgThemeCall: TextureVideoView
    private lateinit var imgAcceptCall: ImageView
    private lateinit var imgRejectCall:ImageView
    private lateinit var imgExit:ImageView
    private lateinit var imgAva: CircleImageView
    private lateinit var imgBgThemeCall: DynamicImageView
    private lateinit var txtName: TextView
    private lateinit var txtPhone:TextView
    private var typeBgCall = 0
    private lateinit var themeSelect: Theme
    private lateinit var theme_contact:Theme
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var telephonyService: ITelephony
    private val ID_NOTIFICATION = 1
    var isDisable = false
    private var bmpAvatar: Bitmap? = null
    private var mWindowManager: WindowManager? = null
    private var mLocalBroadcastManager: LocalBroadcastManager? = null
    private var name: String? = null
    var mLayoutParams: WindowManager.LayoutParams? = null
    private var contactId = ""
    private var mContact: Contact? = null
    private lateinit var inflater: LayoutInflater

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(ID_NOTIFICATION, NotifiUtil.initNotificationAndroidQ(this))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(ID_NOTIFICATION, NotifiUtil.initNotificationAndroidO(this))
        }
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (intent != null && intent.extras != null) {
            try {
                phone = intent.getStringExtra(Constant.PHONE_NUMBER).toString()
            } catch (e: Exception) {
                e.message
            }
            phone = phone!!.replace(" ".toRegex(), "").replace("-".toRegex(), "")
            checkDevice()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun checkDevice() {
        if (Build.MANUFACTURER != null && (Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)
                    || Build.MANUFACTURER.equals("realme", ignoreCase = true))
            || Build.MANUFACTURER.contains("INFINIX")
        ) {
            showViewCallColor()
        } else {
            val intent2 = Intent(applicationContext, IncommingCallActivity::class.java)
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent2.putExtra(Constant.PHONE_NUMBER, phone)
            startActivity(intent2)
        }
        // showViewCallColor();
    }

    override fun onDestroy() {
        Handler().postDelayed({ removeUI() }, 500)
        mLocalBroadcastManager!!.unregisterReceiver(mBroadcastReceiver)
        NotifiUtil.hideNotification(this)
        super.onDestroy()
    }

    fun removeUI() {
        try {
            if (viewCall != null && mWindowManager != null) {
                mWindowManager!!.removeView(viewCall)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showViewCallColor() {
        themeSelect = HawkData.getThemeSelect()
        try {
            initLayoutColor()
            viewCall = inflater.inflate(R.layout.layout_call_incomming, null)
            txtPhone = viewCall.findViewById(R.id.txtPhone)
            imgAva = viewCall.findViewById(R.id.profile_image)
            txtName = viewCall.findViewById(R.id.txtName)
            imgAcceptCall = viewCall.findViewById(R.id.btnAccept)
            imgRejectCall = viewCall.findViewById(R.id.btnReject)
            imgExit = viewCall.findViewById(R.id.imgExit)
            Glide.with(this).load(R.drawable.ic_exit).into(imgExit)
            imgExit.setOnClickListener {
                viewCall.visibility = View.GONE
                removeUI()
                stopCallService()
            }
            imgBgThemeCall = viewCall!!.findViewById(R.id.img_theme_call)
            vdoBgThemeCall = viewCall!!.findViewById(R.id.vd_theme_call)
            typeBgCall = themeSelect.type
            try {
                val contactRetrieve: ContactRetrieve =
                    AppUtil.getContactName(applicationContext, phone)
                name = contactRetrieve.name
                contactId = contactRetrieve.contact_id
                txtName.setText(name)
                if (name == "") {
                    txtName.setText(getString(R.string.unknowContact))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bmpAvatar = AppUtil.getPhotoContact(applicationContext, phone)
            imgAva.setImageBitmap(bmpAvatar)
            txtPhone.setText(phone.toString())
            vdoBgThemeCall.setVisibility(View.VISIBLE)
            viewCall!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            mWindowManager!!.addView(viewCall, mLayoutParams)
           // analystic.trackEvent(ManagerEvent.callWinDowShow())
            RoomManager.get().liveContactListWithId(contactId).observe(this) {
                val listQueryContactID: List<Contact> = it
                if (listQueryContactID.isNotEmpty()) {
                    mContact = listQueryContactID[0]
                    theme_contact =
                        Gson().fromJson(mContact!!.theme, Theme::class.java)
                }
                themeSelect = theme_contact
                typeBgCall = theme_contact.type
                checkTypeCall(typeBgCall)
            }
            Handler().postDelayed({ startAnimation() }, 400)
            handlingCallState()
            listener()
        } catch (e: Exception) {
            stopCallService()
        }
    }

    private fun initLayoutColor() {
        val LAYOUT_TYPE: Int
        LAYOUT_TYPE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        }
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mLayoutParams = WindowManager.LayoutParams()
        mLayoutParams!!.type = LAYOUT_TYPE
        mLayoutParams!!.format = -2
        mLayoutParams!!.flags = 524584
        mLayoutParams!!.width = ViewGroup.LayoutParams.MATCH_PARENT
        mLayoutParams!!.height = ViewGroup.LayoutParams.MATCH_PARENT
        mLayoutParams!!.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mLayoutParams!!.windowAnimations = 16973826
        inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onCreate() {
       // analystic = Analystic.getInstance(this)
        //analystic.trackEvent(ManagerEvent.callServiceOncreate())
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)
        val mIntentFilter = IntentFilter()
        mIntentFilter.addAction("com.colorcall.endCall")
        mLocalBroadcastManager!!.registerReceiver(mBroadcastReceiver, mIntentFilter)
        super.onCreate()
    }

    var mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val t: Thread = object : Thread() {
                override fun run() {
                    if (intent.action == "com.colorcall.endCall") {
                        stopCallService()
                    }
                }
            }
            t.start()
        }
    }

    private fun checkTypeCall(typeBgCall: Int) {
        when (typeBgCall) {
            Constant.TYPE_VIDEO -> handleThemeVideo()
            Constant.TYPE_IMAGE -> handleCallImage()
        }
    }

    fun startAnimation() {
        val anim8 = AnimationUtils.loadAnimation(this, R.anim.ani_bling_call)
        imgAcceptCall!!.startAnimation(anim8)
    }

    private fun handlingCallState() {
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val clazz: Class<*>
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                clazz = Class.forName(telephonyManager!!.javaClass.name)
                val method = clazz.getDeclaredMethod("getITelephony")
                method.isAccessible = true
                telephonyService = method.invoke(telephonyManager) as ITelephony
            }
        } catch (e: Exception) {
            stopCallService()
            e.printStackTrace()
        }
    }

    private fun listener() {
        imgAcceptCall.setOnClickListener { v: View? ->
           // analystic.trackEvent(ManagerEvent.callWinDowAcceptCall())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val tm =
                    getSystemService(TELECOM_SERVICE) as TelecomManager
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ANSWER_PHONE_CALLS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@setOnClickListener
                }
                tm.acceptRingingCall()
            } else {
                val intent = Intent(applicationContext, AcceptCallActivity::class.java)
                intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                applicationContext.startActivity(intent)
            }
            isDisable = true
            stopCallService()
        }
        imgRejectCall.setOnClickListener(View.OnClickListener { v: View? ->
           // analystic.trackEvent(ManagerEvent.callWinDowRejectCall())
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val tm =
                        applicationContext.getSystemService(TELECOM_SERVICE) as TelecomManager
                    tm.endCall()
                } else {
                    telephonyService.endCall()
                }
                isDisable = true
                stopCallService()
            } catch (e: Exception) {
                stopCallService()
            }
        })
    }

    private fun handleThemeVideo() {
        val sPath: String
        imgBgThemeCall.setVisibility(View.GONE)
        vdoBgThemeCall.setVisibility(View.VISIBLE)
        sPath =
            if (themeSelect.path_file.contains("storage") || themeSelect.path_file
                    .contains("/data/data") || themeSelect.path_file.contains("data/user/")
            ) {
                themeSelect.path_file
            } else {
                val uriPath = "android.resource://" + packageName + themeSelect.path_file
                uriPath
            }
        vdoBgThemeCall.setVideoURI(Uri.parse(sPath))
        vdoBgThemeCall.setOnErrorListener { mp, what, extra ->
            //analystic.trackEvent(ManagerEvent.callVideoViewError(what, extra))
            stopCallService()
            true
        }
        vdoBgThemeCall.setOnPreparedListener { mp ->
            mp.setLooping(true)
            mp.setVolume(0.0f, 0.0f)
            vdoBgThemeCall.start()
        }
    }

    private fun handleCallImage() {
        imgBgThemeCall.setVisibility(View.VISIBLE)
        val sPathThumb: String
        sPathThumb =
            if (themeSelect.path_file.contains("default") && themeSelect.path_file
                    .contains("thumbDefault")
            ) {
                "file:///android_asset/" + themeSelect.path_file
            } else {
                themeSelect.path_file
            }
        Glide.with(applicationContext)
            .load(sPathThumb)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .thumbnail(0.1f)
            .into(imgBgThemeCall)
        vdoBgThemeCall.setVisibility(View.GONE)
    }

    fun stopCallService() {
        applicationContext.stopService(
            Intent(
                applicationContext,
               ColorCallService::class.java
            )
        )
    }
}