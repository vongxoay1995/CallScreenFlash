package com.call.colorscreen.ledflash.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.call.colorscreen.ledflash.util.AppAdsId
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Date

class InterstitialApply {
    private var interstitialAd: InterstitialAd? = null
    private lateinit var activity: Activity
    private var ID_ADS = ""
    private var listener: InterstitialAdListener? = null
    var onAdClosed: (() -> Unit)? = null
    var isLoaded = false
        private set
    var isAdLoadFail = false
        private set
    var isShowAds = false
    var isLoading = false
    private var loadTime: Long = 0
    constructor(activity: Activity, ID_ADS: String) {
        this.activity = activity
        this.ID_ADS = ID_ADS
    }
    constructor(activity: Activity) {
        this.activity = activity
    }

    constructor(activity: Activity, ID_ADS: String, listener: InterstitialAdListener?) {
        this.activity = activity
        this.ID_ADS = ID_ADS
        this.listener = listener
    }

    constructor(activity: Activity, listener: InterstitialAdListener?) {
        this.activity = activity
        this.listener = listener
    }

    fun setID_ADS(ID_ADS: String) {
        this.ID_ADS = ID_ADS
    }

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    fun setListener(listener: InterstitialAdListener?) {
        this.listener = listener
    }

    fun loadAds() {
        if (isAdAvailable()) {
            return
        }
        isLoaded = false
        isAdLoadFail = false
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            ID_ADS,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    isLoaded = true
                    isLoading = false
                    loadTime = Date().time
                    this@InterstitialApply.interstitialAd = interstitialAd
                    if (listener != null) {
                        listener!!.onAdLoaded(interstitialAd)
                    }
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            isLoaded = false
                            isShowAds = false
                            this@InterstitialApply.interstitialAd = null
                            onAdClosed?.invoke()
                            loadAds()
                            if (listener != null) {
                                listener!!.onAdDismissedFullScreenContent()
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            this@InterstitialApply.interstitialAd = null
                            isShowAds = false
                            isLoaded = false
                            if (listener != null) {
                                listener!!.onAdFailedToShowFullScreenContent(adError)
                            }
                        }

                        override fun onAdShowedFullScreenContent() {
                            isShowAds = true
                            if (listener != null) {
                                listener!!.onAdShowedFullScreenContent()
                            }
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    interstitialAd = null
                    isAdLoadFail = true
                    isLoading = false
                    if (listener != null) {
                        listener!!.onAdFailedToLoad(loadAdError)
                    }
                }
            })
    }
    fun isShowAdsInter():Boolean{
        return isShowAds
    }
    fun isAdAvailable(): Boolean {
        return interstitialAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - this.loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < (numMilliSecondsPerHour * numHours)
    }
    fun showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd!!.show(activity)
        }
    }
    companion object{
        @SuppressLint("StaticFieldLeak")
        private var sInterstitial: InterstitialApply?=null
        fun getInstance(activity: Activity): InterstitialApply {
            if (sInterstitial == null) {
                sInterstitial = InterstitialApply(activity, AppAdsId.inter_apply)
            }
            return sInterstitial as InterstitialApply
        }
    }
}