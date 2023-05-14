package com.call.colorscreen.ledflash.ads

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.*

class BannerAdsUtils {
    private var activity: Activity
    private var layoutBannerAds: ViewGroup
    private var adsListener: BannerAdsListener? = null
    private var admobId: String? = null
    private var adviewGoogle: AdView? = null
    var goneWhenFail = true

    constructor(activity: Activity, admobId: String?, viewContainer: ViewGroup) {
        this.activity = activity
        this.admobId = admobId
        layoutBannerAds = viewContainer
    }

    constructor(activity: Activity, viewContainer: ViewGroup) {
        this.activity = activity
        layoutBannerAds = viewContainer
    }

    constructor(activity: Activity, admobId: String?, viewContainer: ViewGroup, adsListener: BannerAdsListener?) {
        this.activity = activity
        layoutBannerAds = viewContainer
        this.adsListener = adsListener
        this.admobId = admobId
    }

    constructor(activity: Activity, viewContainer: ViewGroup, adsListener: BannerAdsListener?) {
        this.activity = activity
        layoutBannerAds = viewContainer
        this.adsListener = adsListener
    }

    fun setAdsListener(adsListener: BannerAdsListener) {
        this.adsListener = adsListener
    }


    fun loadAds() {
        adviewGoogle = AdView(activity)
        val adRequestBuilder = AdRequest.Builder()
        val adSize = adSize
        adviewGoogle!!.adSize = adSize
        adviewGoogle!!.adUnitId = admobId
        adviewGoogle!!.loadAd(adRequestBuilder.build())
        adviewGoogle!!.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                if(goneWhenFail) {
                    layoutBannerAds.removeAllViews()
                    layoutBannerAds.visibility = View.GONE
                }
                super.onAdFailedToLoad(loadAdError)
                if (adsListener != null) {
                    adsListener!!.onAdFailedToLoad(loadAdError)
                }
            }

            override fun onAdLoaded() {
                layoutBannerAds.visibility = View.VISIBLE
                layoutBannerAds.removeAllViews()
                layoutBannerAds.addView(adviewGoogle)
                super.onAdLoaded()
                if (adsListener != null) {
                    adsListener!!.onAdLoaded()
                }
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                if (adsListener != null) {
                    adsListener!!.onAdClicked()
                }
            }

            override fun onAdClosed() {
                super.onAdClosed()
                if (adsListener != null) {
                    adsListener!!.onAdClosed()
                }
            }
        }
    }

    private val adSize: AdSize
        private get() {
            val display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density
            val adWidth = (widthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        }
}