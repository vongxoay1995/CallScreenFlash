package com.call.colorscreen.ledflash.ui.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.databinding.ActivitySettingBinding
import com.call.colorscreen.ledflash.util.*
import com.facebook.ads.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
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
    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun onViewReady(savedInstance: Bundle?) {
        AppUtil.overHeader(this, binding.layoutHeader)
        binding.swOnOff.isChecked = HawkData.getEnableCall()
        binding.swflash.isChecked = HawkData.getEnableFlash()
        listener()
        //loadAds()
    }

    private fun listener() {
        binding.swOnOff.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            isCallState = isChecked
            Log.e("TAN", "listener swOnOff: "+isAllowCallScreen )
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
    }

    private fun loadAds() {
        val ID_ADS_GG = "ca-app-pub-3940256099942544/2247696110"
        val builder = AdLoader.Builder(this, ID_ADS_GG)
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
                finish()
            }
            R.id.llShare -> {
                val sendIntent = Intent()
                sendIntent.action = "android.intent.action.SEND"
                val sb2 = StringBuilder()
                sb2.append(Constant.PLAY_STORE_LINK)
                sb2.append(packageName)
                sendIntent.putExtra("android.intent.extra.TEXT", sb2.toString())
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            R.id.llRate -> {
                val appPackageName = packageName
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (ex: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }
            R.id.llPolicy -> {
                openLink("")
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
            Handler().postDelayed({ isAllowCallScreen = false },100);
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
}