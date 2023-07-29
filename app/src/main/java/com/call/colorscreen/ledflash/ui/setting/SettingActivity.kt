package com.call.colorscreen.ledflash.ui.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.ads.BannerAdsListener
import com.call.colorscreen.ledflash.ads.BannerAdsUtils
import com.call.colorscreen.ledflash.analystic.Analystic
import com.call.colorscreen.ledflash.analystic.ManagerEvent
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.databinding.ActivitySettingBinding
import com.call.colorscreen.ledflash.databinding.LayoutBottomSheetRateBinding
import com.call.colorscreen.ledflash.util.*
import com.call.colorscreen.ledflash.util.Constant.MAIL_LIST
import com.call.colorscreen.ledflash.util.Constant.PLAY_STORE_LINK
import com.call.colorscreen.ledflash.util.Constant.RATE_FEED_BACK
import com.call.colorscreen.ledflash.util.Constant.RATE_IN_APP
import com.call.colorscreen.ledflash.util.Constant.RATE_LATER
import com.call.colorscreen.ledflash.view.AnimationRatingBar
import com.facebook.ads.*
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import java.util.*

class SettingActivity : BaseActivity<ActivitySettingBinding>(),
    View.OnClickListener, PermissionCallListener,
    PermissionFlashListener {
    private var nativeAd: NativeAd? = null
    private var nativeFb: com.facebook.ads.NativeAd? = null
    private var adView: LinearLayout? = null
    private var isFlashState = false
    private var isCallState = false
    private var isAllowFlash = false
    private var isAllowCallScreen = false
    private lateinit var analystic: Analystic
    private var bottomSheetDialog: BottomSheetDialog? = null
    var TYPE_RATE = RATE_LATER
    private lateinit var bannerAdsUtils: BannerAdsUtils

    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun onViewReady(savedInstance: Bundle?) {
        AppUtil.overHeader(this, binding.layoutHeader)
        binding.swOnOff.isChecked = HawkData.getEnableCall()
        binding.swflash.isChecked = HawkData.getEnableFlash()
        listener()
        if (AppUtil.checkInternet(this)) {
            loadBannerAds()
        } else {
            binding.llAds.visibility = View.GONE;
        }
        //loadAds()
        analystic = Analystic.getInstance(this)
        analystic.trackEvent(ManagerEvent.settingShow())
    }

    private fun loadBannerAds() {
        bannerAdsUtils = BannerAdsUtils(this, AppAdsId.id_banner_setting, binding.llAds)
        bannerAdsUtils.loadAds()
        bannerAdsUtils.goneWhenFail = false
        bannerAdsUtils.setAdsListener(object : BannerAdsListener() {

            override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                super.onAdFailedToLoad(loadAdError)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
            }

            override fun onAdClicked() {
                super.onAdClicked()
            }
        })
    }

    private fun listener() {
        binding.swOnOff.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            isCallState = isChecked
            if (!isAllowCallScreen) {
                PermissionUtil.checkPermissionCall(this@SettingActivity, this)
            } else {
                isAllowCallScreen = false
            }
        }
        binding.swflash.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            isFlashState = isChecked
            if (!isAllowFlash) {
                PermissionUtil.checkPermissionFlash(this@SettingActivity, this)
            } else {
                isAllowFlash = false
            }
        }
        binding.btnBack.setOnClickListener(this)
        binding.llPolicy.setOnClickListener(this)
        binding.llRate.setOnClickListener(this)
        binding.llShare.setOnClickListener(this)
        binding.llFlash.setOnClickListener(this)
    }

    private fun loadAds() {
        val builder = AdLoader.Builder(this, AppAdsId.id_native_setting)
            .forNativeAd { nativeAd: NativeAd ->
                val isDestroyed = isDestroyed
                if (isDestroyed || isFinishing || isChangingConfigurations) {
                    nativeAd.destroy()
                    return@forNativeAd
                }
                // otherwise you will have a memory leak.
                nativeAd.destroy()
                this.nativeAd = nativeAd
                val adView = layoutInflater.inflate(
                    R.layout.ad_unified,
                    null
                ) as NativeAdView
                AppUtil.populateNativeAdView(nativeAd, adView)
                binding.llAdsGG.removeAllViews()
                binding.llAdsGG.addView(adView)
            }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    binding.llAdsGG.visibility = View.GONE
                    loadAdsFb()
                }
            })
        val adLoader = builder.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun loadAdsFb() {
        nativeFb = NativeAd(this, "YOUR_PLACEMENT_ID")
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {}
            override fun onError(ad: Ad, adError: com.facebook.ads.AdError) {}
            override fun onAdLoaded(ad: Ad) {
                if (nativeFb == null || nativeFb !== ad) {
                    return
                }
                // Inflate Native Ad into Container
                inflateAd(nativeFb!!)
            }

            override fun onAdClicked(ad: Ad) {}
            override fun onLoggingImpression(ad: Ad) {}
        }

        // Request an ad
        nativeFb!!.loadAd(
            nativeFb!!.buildLoadAdConfig()
                .withAdListener(nativeAdListener)
                .build()
        )
    }

    private fun inflateAd(nativeFb: com.facebook.ads.NativeAd) {
        nativeFb.unregisterView()
        val inflater = LayoutInflater.from(this)
        adView = inflater.inflate(R.layout.native_ad_layout, binding.llAdsFb, false) as LinearLayout
        binding.llAdsFb.addView(adView)

        // Add the AdOptionsView
        val adChoicesContainer = findViewById<LinearLayout>(R.id.ad_choices_container)
        val adOptionsView = AdOptionsView(this, nativeFb, binding.llAdsFb)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        val nativeAdIcon: MediaView = adView!!.findViewById(R.id.native_ad_icon)
        val nativeAdTitle: TextView = adView!!.findViewById(R.id.native_ad_title)
        val nativeAdMedia: MediaView = adView!!.findViewById(R.id.native_ad_media)
        val nativeAdSocialContext: TextView =
            adView!!.findViewById(R.id.native_ad_social_context)
        val nativeAdBody: TextView = adView!!.findViewById(R.id.native_ad_body)
        val sponsoredLabel: TextView = adView!!.findViewById(R.id.native_ad_sponsored_label)
        val nativeAdCallToAction: Button =
            adView!!.findViewById(R.id.native_ad_call_to_action)

        // Set the Text.
        nativeAdTitle.text = nativeFb.advertiserName
        nativeAdBody.text = nativeFb.adBodyText
        nativeAdSocialContext.text = nativeFb.adSocialContext
        nativeAdCallToAction.visibility =
            if (nativeFb.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeFb.adCallToAction
        sponsoredLabel.text = nativeFb.sponsoredTranslation

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeFb.registerViewForInteraction(
            adView, nativeAdMedia, nativeAdIcon, clickableViews
        )
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnBack -> {
                analystic.trackEvent(ManagerEvent.settingBackClick())
                finish()
            }
            R.id.llShare -> {
                analystic.trackEvent(ManagerEvent.settingShareAppClick())
                val sendIntent = Intent()
                sendIntent.action = "android.intent.action.SEND"
                val sb2 = StringBuilder()
                sb2.append(PLAY_STORE_LINK)
                sb2.append(packageName)
                sendIntent.putExtra("android.intent.extra.TEXT", sb2.toString())
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            R.id.llRate -> {
                analystic.trackEvent(ManagerEvent.settingRateClick())
                if (HawkData.isRated()!!) {
                    goToStoreApp()
                } else {
                    initBottomSheetRate()
                }
            }
           /* R.id.llFlash -> {
                analystic.trackEvent(ManagerEvent.settingFlashClick())
               Toast.makeText(this,"Developing",Toast.LENGTH_SHORT).show();
            }*/
            R.id.llPolicy -> {
                analystic.trackEvent(ManagerEvent.settingPolicyClick())
                openLink("https://sites.google.com/view/privacy-policy-for-call-color")
            }
        }
    }

    private fun openLink(url: String?) {
        try {
            val intentUpdate = Intent(Intent.ACTION_VIEW)
            intentUpdate.data = Uri.parse(url)
            startActivity(intentUpdate)
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
        }
    }
    private fun goToStoreApp() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val linkRateApp: String = PLAY_STORE_LINK + packageName
            intent.data = Uri.parse(linkRateApp)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        if (nativeAd != null) {
            nativeAd!!.destroy()
        }
        nativeFb?.destroy()
        super.onDestroy()
    }

    override fun onHasCall() {
        HawkData.setEnableCall(isCallState)
    }

    override fun onHasFlash() {
        HawkData.setEnableFlash(isFlashState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if (requestCode == Constant.PERMISSION_REQUEST_CAMERA) {
             if (grantResults.isNotEmpty() && AppUtil.checkPermission(grantResults)) {
                 HawkData.setEnableFlash(isFlashState)
             } else {
                isAllowFlash = true
                 binding.swflash.isChecked = !isFlashState
             }
         } else if (requestCode == Constant.PERMISSION_REQUEST_CALL_PHONE) {
            if (grantResults.isNotEmpty() && AppUtil.checkPermission(grantResults)) {
                if (AppUtil.canDrawOverlays(this)) {
                    if (!AppUtil.checkNotificationAccessSettings(this)) {
                        resetOnOffCall()
                        AppUtil.showNotificationAccess(this)
                    }
                } else {
                    resetOnOffCall()
                    AppUtil.checkDrawOverlayApp(this)
                }
            } else {
                resetOnOffCall()
            }
        }
    }

    private fun resetOnOffCall() {
        isAllowCallScreen = true
        binding.swOnOff.isChecked = !isCallState
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("TAN", "onActivityResult: aaaaaaaa")
        if (!AppUtil.canDrawOverlays(this) || !AppUtil.checkNotificationAccessSettings(this)) {
            isAllowCallScreen = true
            binding.swOnOff.isChecked = false
            Handler().postDelayed({ isAllowCallScreen = false }, 100);
        }
        if (requestCode == Constant.REQUEST_DRAW_OVER) {
            if (AppUtil.canDrawOverlays(this)) {
                if (!AppUtil.checkNotificationAccessSettings(this)) {
                    isCallState = true
                    resetOnOffCall()
                    Log.e("TAN", "onActivityResult: showNotificationAccess")

                    AppUtil.showNotificationAccess(this)
                }
            }
        } else if (requestCode == Constant.REQUEST_NOTIFICATION) {
            if (AppUtil.checkNotificationAccessSettings(this)) {
                isCallState = true
                binding.swOnOff.isChecked = true
                onHasCall()
            }
        }
    }
    private fun initBottomSheetRate() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
        val bottomBinding: LayoutBottomSheetRateBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.layout_bottom_sheet_rate, null, false)
        bottomSheetDialog!!.setContentView(bottomBinding.root)
        bottomSheetDialog!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog!!.show()
        bottomBinding.tvRate.setOnClickListener {
            when (TYPE_RATE) {
                RATE_IN_APP -> {
                    rateInApp()
                }
                RATE_FEED_BACK -> {
                    sendFeedBack()
                }
            }
            bottomSheetDialog!!.dismiss()
        }
        bottomSheetDialog!!.setOnDismissListener {
        }
        Handler().postDelayed({
            bottomBinding.ratingbar.setRating(5, true)
        }, 500)
        bottomBinding.ratingbar.setListener(object : AnimationRatingBar.Listener {
            override fun getRate(rate: Int) {
                when (rate) {
                    5 -> {
                        TYPE_RATE = RATE_IN_APP
                        bottomBinding.icRate.setImageDrawable(
                            ContextCompat
                            .getDrawable(this@SettingActivity, R.drawable.image_rate_happy))
                        bottomBinding.tvRate.text = getString(R.string.rate_on_gg_play)
                        bottomBinding.tvContentRate.text = getString(R.string.rate_title3)
                        bottomBinding.tvGuideRate.text = getString(R.string.rate_content3)
                        bottomBinding.tvRate.setTextColor(ContextCompat.getColor(this@SettingActivity, R.color.white))
                        bottomBinding.tvRate.background =
                            ContextCompat.getDrawable(this@SettingActivity, R.drawable.bg_button_rate)
                    }
                    0 -> {
                        TYPE_RATE = RATE_LATER
                    }
                    else -> {
                        TYPE_RATE = RATE_FEED_BACK
                        bottomBinding.icRate.setImageDrawable(
                            ContextCompat
                            .getDrawable(this@SettingActivity, R.drawable.image_rate_sad))
                        bottomBinding.tvRate.text = getString(R.string.feed_back_rate)
                        bottomBinding.tvContentRate.text = getString(R.string.rate_title2)
                        bottomBinding.tvGuideRate.text = getString(R.string.rate_content2)
                        bottomBinding.tvRate.setTextColor(ContextCompat.getColor(this@SettingActivity, R.color.white))
                        bottomBinding.tvRate.background =
                            ContextCompat.getDrawable(this@SettingActivity, R.drawable.bg_button_rate)
                    }
                }
            }
        })
    }
    private fun rateInApp() {
        val reviewManager: ReviewManager = ReviewManagerFactory.create(this)
        val request: com.google.android.play.core.tasks.Task<ReviewInfo> =
            reviewManager.requestReviewFlow()
        request.addOnSuccessListener { result ->
            val flow: com.google.android.play.core.tasks.Task<Void> =
                reviewManager.launchReviewFlow(this, result)
            flow.addOnSuccessListener {
                HawkData.setRate(true)
            }
        }.addOnFailureListener { goToStoreApp() }
    }
    private fun sendFeedBack() {
        val mailSubject = getString(R.string.mail_subject)
        val mailContent = ""
        val mailIntent = Intent(Intent.ACTION_SEND)
        mailIntent.type = "text/email"
        mailIntent.putExtra(Intent.EXTRA_EMAIL, MAIL_LIST)
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject)
        mailIntent.putExtra(Intent.EXTRA_TEXT, mailContent)
        if (packageManager.getLaunchIntentForPackage("com.google.android.gm") != null) {
            mailIntent.setPackage("com.google.android.gm")
        }
        startActivity(Intent.createChooser(mailIntent, "$mailSubject:"))
    }
    override fun onCreate() {

    }
}