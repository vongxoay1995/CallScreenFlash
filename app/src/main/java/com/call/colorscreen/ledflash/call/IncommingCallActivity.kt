package com.call.colorscreen.ledflash.call

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.internal.telephony.ITelephony
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.analystic.Analystic
import com.call.colorscreen.ledflash.analystic.ManagerEvent
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.database.Contact
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.ActivityIncommingCallBinding
import com.call.colorscreen.ledflash.service.AcceptCallActivity
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.ContactRetrieve
import com.call.colorscreen.ledflash.util.HawkData
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import java.lang.reflect.Method


class IncommingCallActivity : BaseActivity<ActivityIncommingCallBinding>(), View.OnClickListener {
    var mLocalBroadcastManager: LocalBroadcastManager? = null
    private var phoneNumber = ""
    private var themeSelect: Theme? = null
    private var theme_contact:Theme? = null
    private var typeThemeCall = 0
    private var name: String? = null
    private var contactId = ""
    private var bmpAvatar: Bitmap? = null
    private var mContact: Contact? = null
    val database by inject<AppDatabase>()
    private var telephonyManager: TelephonyManager? = null
    private var telephonyService: ITelephony? = null
    var isDisable = false
    private lateinit var analystic: Analystic

    var mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == "com.callcolor.endCall") {
                finish()
            }
        }
    }
    override fun getLayoutId(): Int {
        return com.call.colorscreen.ledflash.R.layout.activity_incomming_call
    }
    override fun onViewReady(savedInstance: Bundle?) {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)
        val mIntentFilter = IntentFilter()
        mIntentFilter.addAction("com.callcolor.endCall")
        mLocalBroadcastManager!!.registerReceiver(mBroadcastReceiver, mIntentFilter)
        showLayoutCall()
        analystic = Analystic.getInstance(this)
        analystic.trackEvent(ManagerEvent.callshow())
    }

    override fun onClick(p0: View?) {

    }

    override fun onCreate() {
        window.setFlags(1024, 1024)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocalBroadcastManager!!.unregisterReceiver(mBroadcastReceiver)
    }
    private fun showLayoutCall() {
        phoneNumber = intent.getStringExtra(Constant.PHONE_NUMBER).toString()
        themeSelect = HawkData.getThemeSelect()
        themeSelect?.let {

        }
        if (themeSelect != null) {
            typeThemeCall = themeSelect!!.type
            try {
                val contactRetrieve: ContactRetrieve =
                    AppUtil.getContactName(applicationContext, phoneNumber)
                name = contactRetrieve.name
                contactId = contactRetrieve.contact_id
                binding.txtName.text = name
                if (name.equals("")) {
                    binding.txtName.text = getString(com.call.colorscreen.ledflash.R.string.unknowContact)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bmpAvatar = AppUtil.getPhotoContact(baseContext, phoneNumber)
            binding.imgAvatar.setImageBitmap(bmpAvatar)
            binding.txtPhone.text = phoneNumber
            binding.videoTheme.visibility = View.VISIBLE
            val listQueryContactID = database.serverDao().getContactById(contactId)
            if (listQueryContactID.isNotEmpty()) {
                mContact = listQueryContactID[0]
                theme_contact =
                    Gson().fromJson(mContact!!.theme, Theme::class.java)
            }
            if (theme_contact != null) {
                themeSelect = theme_contact
                typeThemeCall = theme_contact!!.type
            }
            checkTypeTheme(typeThemeCall)
            Handler().postDelayed(this::startAnimation, 400)
            handlingCallState()
            listener()
        }
    }

    @SuppressLint("MissingPermission")
    private fun listener() {
        binding.imgAccept.setOnClickListener { v ->
            analystic.trackEvent(ManagerEvent.callAcceptCall())
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
                val intent =
                    Intent(applicationContext, AcceptCallActivity::class.java)
                intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                applicationContext.startActivity(intent)
            }
            finish()
        }

        binding.imgReject.setOnClickListener { v ->
            analystic.trackEvent(ManagerEvent.callRejectCall())
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val tm =
                        applicationContext.getSystemService(TELECOM_SERVICE) as TelecomManager
                    tm.endCall()
                } else {
                    telephonyService!!.endCall()
                }
                finish()
            } catch (e: java.lang.Exception) {
                finish()
            }
        }
    }

    private fun checkTypeTheme(typeBgCall: Int) {
        when (typeBgCall) {
            Constant.TYPE_VIDEO -> handlingBgCallVideo()
            Constant.TYPE_IMAGE -> handlingBgCallImage()
        }
    }
    fun startAnimation() {
        val anim8: Animation = AnimationUtils.loadAnimation(this, com.call.colorscreen.ledflash.R.anim.ani_bling_call)
        binding.imgAccept.startAnimation(anim8)
    }

    private fun handlingCallState() {
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val clazz: Class<*>
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                clazz = Class.forName(telephonyManager!!.javaClass.name)
                val method: Method = clazz.getDeclaredMethod("getITelephony")
                method.isAccessible = true
                telephonyService = method.invoke(telephonyManager) as ITelephony
            }
        } catch (e: java.lang.Exception) {
            finish()
            e.printStackTrace()
        }
    }
    private fun handlingBgCallVideo() {
        val sPath: String
        binding.imgThemeCall.visibility = View.GONE
        binding.videoTheme.visibility = View.VISIBLE
        sPath =
            if (themeSelect?.path_file!!.contains("storage") || themeSelect!!.path_file
                    .contains("/data/data") || themeSelect!!.path_file.contains("data/user/")
            ) {
                themeSelect!!.path_file
            } else {
                val uriPath = "android.resource://" + packageName + themeSelect!!.path_file
                uriPath
            }
        binding.videoTheme.setVideoURI(Uri.parse(sPath))
        binding.videoTheme.setOnErrorListener { mp, what, extra ->
            analystic.trackEvent(ManagerEvent.callVideoViewError(what, extra))
            finish()
            true
        }
        binding.videoTheme.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0.0f, 0.0f)
            binding.videoTheme.start()
        }
    }

    private fun handlingBgCallImage() {
        binding.imgThemeCall.visibility = View.VISIBLE
        val sPathThumb: String
        sPathThumb =
            if (themeSelect!!.path_file.contains("default") && themeSelect!!.path_file
                    .contains("thumbDefault")
            ) {
                "file:///android_asset/" + themeSelect!!.path_file
            } else {
                themeSelect!!.path_file
            }
        Glide.with(applicationContext)
            .load(sPathThumb)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .thumbnail(0.1f)
            .into(binding.imgThemeCall)
        binding.videoTheme.setVisibility(View.GONE)
    }
}