package com.call.colorscreen.ledflash.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.android.internal.telephony.ITelephony
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.analystic.Analystic
import com.call.colorscreen.ledflash.analystic.ManagerEvent
import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.database.Contact
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.receive.PhoneStateListenerHighAPI
import com.call.colorscreen.ledflash.receive.PhoneStateListenerLowAPI
import com.call.colorscreen.ledflash.service.AcceptCallActivity
import com.call.colorscreen.ledflash.service.DynamicImageView
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.ContactRetrieve
import com.call.colorscreen.ledflash.util.HawkData
import com.facebook.FacebookSdk
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView

class RingingCallView : RelativeLayout {
    private var context: Context
    var windowManager: WindowManager? = null
    var number :String = ""
    var windowParams: WindowManager.LayoutParams? = null
    var themeSelect: Theme? = null
    var themeContact: Theme? = null
    private var typeBgCall = 0
    private var name: String? = null
    private var contactId = ""
    private var mContact: Contact? = null
    private var bmpAvatar: Bitmap? = null
    private var analystic: Analystic
    private var telephonyManager: TelephonyManager? = null
    private var telephonyService: ITelephony? = null

   // var phoneStateLowAPI: PhoneStateListenerLowAPI? = null
   // var phoneStateHighAPI: PhoneStateListenerHighAPI? = null
    var database: AppDatabase ? = null

    @BindView(R.id.txtName)
    lateinit var txtName: TextView

    @BindView(R.id.txtPhone)
    lateinit var txtPhone: TextView

    @BindView(R.id.img_avatar)
    lateinit var imgAvatar: CircleImageView

    @BindView(R.id.imgExit)
    lateinit var imgExit: ImageView

    @BindView(R.id.vdo_theme_call)
    lateinit var vdo_theme_call: TextureVideoView

    @BindView(R.id.img_bg_call)
    lateinit var img_bg_call: DynamicImageView

    @BindView(R.id.btnAccept)
    lateinit var btnAccept: ImageView

    @BindView(R.id.imgReject)
    lateinit var imgReject: ImageView
    fun setDatabaseApp(database: AppDatabase) {
        this.database = database;
    }

    constructor(context: Context) : super(context) {
        this.context = context
        analystic = Analystic.getInstance(context)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        this.context = context
        analystic = Analystic.getInstance(context)
    }

    constructor(context: Context, attributeSet: AttributeSet?, i2: Int) : super(
        context,
        attributeSet,
        i2
    ) {
        this.context = context
        analystic = Analystic.getInstance(context)
    }

    fun setNumberPhone(numberPhone: String?) {
        if (numberPhone != null) {
            this.number = numberPhone
        }
        setInforContact()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.e("TAN", "onFinishInflate: ")
        /*WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.windowParams = layoutParams;
        layoutParams.type = Build.VERSION.SDK_INT >= 26 ? TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        WindowManager.LayoutParams layoutParams2 = this.windowParams;
        layoutParams2.format = -2;
        layoutParams2.flags = 524584;
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        layoutParams2.windowAnimations = 16973826;
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        this.windowManager = windowManager;
        this.windowManager.addView(this, this.windowParams);*/
        val layoutParams = WindowManager.LayoutParams()
        windowParams = layoutParams
        layoutParams.type = if (Build.VERSION.SDK_INT >= 26) 2038 else 2010
        val layoutParams2 = windowParams
        layoutParams2!!.format = -2
        layoutParams2.flags = 524584
        layoutParams2.width = -1
        layoutParams2.height = -1
        layoutParams2.screenOrientation = 1
        layoutParams2.windowAnimations = 16973826
        val windowManager = getContext().getSystemService("window") as WindowManager
        this.windowManager = windowManager
        windowManager.addView(this, windowParams)
        ButterKnife.bind(this,this)
        Log.e("TAN", "onFinishInflate: end")
    }

    fun setInforContact() {
        if (number != null && number != "") {
            try {
                val contactRetrieve: ContactRetrieve = AppUtil.getContactName(
                    FacebookSdk.getApplicationContext(),
                    number.toString()
                )
                name = contactRetrieve.name
                contactId = contactRetrieve.contact_id
                txtName!!.text = name
                if (name == "") {
                    txtName!!.text = context.getString(R.string.unknowContact)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            txtPhone!!.text = number.toString()
            txtPhone!!.visibility = VISIBLE
        } else {
            txtName!!.text = context.getString(R.string.unknowContact)
            txtPhone!!.visibility = INVISIBLE
        }
    }

    fun initData() {
        themeSelect = HawkData.getThemeSelect()
        if (themeSelect != null) {
            typeBgCall = themeSelect!!.type
            bmpAvatar = AppUtil.getPhotoContact(
                FacebookSdk.getApplicationContext(),
                number
            )
            Log.e("TAN", "initData: "+imgAvatar)
            imgAvatar!!.setImageBitmap(bmpAvatar)
            vdo_theme_call!!.visibility = VISIBLE
            Glide.with(this).load(R.drawable.ic_exit).into(imgExit!!)

            val listQueryContactID = database!!.serverDao().getContactById(contactId)

            if (listQueryContactID.size > 0) {
                mContact = listQueryContactID[0]
                themeContact =
                    Gson().fromJson(mContact!!.theme, Theme::class.java)
            }
            if (themeContact != null) {
                themeSelect = themeContact
                typeBgCall = themeContact!!.type
            }
            checkTypeCall(typeBgCall)
            Handler().postDelayed({ this.startAnimation() }, 400)
            handlingCallState()
            listener()
        }
    }

    private fun listener() {
        btnAccept!!.setOnClickListener { v: View? ->
            analystic.trackEvent(ManagerEvent.callAcceptCall())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val tm =
                    context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ANSWER_PHONE_CALLS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@setOnClickListener
                }
                tm?.acceptRingingCall()
            } else {
                val intent =
                    Intent(FacebookSdk.getApplicationContext(), AcceptCallActivity::class.java)
                intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                FacebookSdk.getApplicationContext().startActivity(intent)
            }
            release()
        }
        imgReject!!.setOnClickListener { v: View? ->
            analystic.trackEvent(ManagerEvent.callRejectCall())
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val tm = FacebookSdk.getApplicationContext()
                        .getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    tm?.endCall()
                } else {
                    telephonyService!!.endCall()
                }
                release()
            } catch (e: Exception) {
                release()
            }
        }
        imgExit!!.setOnClickListener { v: View? ->
            analystic.trackEvent(ManagerEvent.callExit())

/*
            if (phoneStateLowAPI != null) {
                phoneStateLowAPI?.release()
            }
            if (phoneStateHighAPI != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    phoneStateHighAPI?.release()
                }
            }*/
            release()
        }
    }

    private fun handlingCallState() {
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val clazz: Class<*>
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                clazz = Class.forName(telephonyManager!!.javaClass.name)
                val method = clazz.getDeclaredMethod("getITelephony")
                method.isAccessible = true
                telephonyService = method.invoke(telephonyManager) as ITelephony
            }
        } catch (e: Exception) {
            release()
            e.printStackTrace()
        }
    }

    private fun handlingBgCallVideo() {
        img_bg_call!!.visibility = GONE
        vdo_theme_call!!.visibility = VISIBLE
        val sPath: String = if (themeSelect?.path_file!!.contains("storage") || themeSelect!!.path_file
                    .contains("/data/data") || themeSelect!!.path_file.contains("data/user/")
            ) {
                themeSelect!!.path_file
            } else {
                val uriPath =
                    "android.resource://" + context.packageName + themeSelect!!.path_file
                uriPath
            }
        vdo_theme_call!!.setVideoURI(Uri.parse(sPath))
        vdo_theme_call!!.setOnErrorListener { mp, what, extra ->
            analystic.trackEvent(ManagerEvent.callVideoViewError(what, extra))
            release()
            true
        }
        vdo_theme_call!!.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0.0f, 0.0f)
            vdo_theme_call!!.start()
        }
    }

    private fun handlingBgCallImage() {
        img_bg_call!!.visibility = VISIBLE
        val sPathThumb: String = if (themeSelect!!.path_file.contains("default") && themeSelect!!.path_file
                    .contains("thumbDefault")
            ) {
                "file:///android_asset/" + themeSelect!!.path_file
            } else {
                themeSelect!!.path_file
            }
        Glide.with(FacebookSdk.getApplicationContext())
            .load(sPathThumb)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .thumbnail(0.1f)
            .into(img_bg_call!!)
        vdo_theme_call!!.visibility = GONE
    }

    private fun checkTypeCall(typeBgCall: Int) {
        when (typeBgCall) {
            Constant.TYPE_VIDEO -> handlingBgCallVideo()
            Constant.TYPE_IMAGE -> handlingBgCallImage()
        }
    }

    fun startAnimation() {
        val anim8 = AnimationUtils.loadAnimation(context, R.anim.ani_bling_call)
        btnAccept!!.startAnimation(anim8)
    }

    fun release() {
        if (windowManager != null) {
            clearView()
            windowManager!!.removeViewImmediate(this)
            windowManager = null
        }
    }

    fun clearView() {
        img_bg_call.setImageDrawable(null)
        img_bg_call.visibility = GONE
        vdo_theme_call.alpha = 0.0f
        vdo_theme_call.stopPlayback()
        vdo_theme_call.visibility = GONE
        try {
            btnAccept.visibility = VISIBLE
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
    }
}
