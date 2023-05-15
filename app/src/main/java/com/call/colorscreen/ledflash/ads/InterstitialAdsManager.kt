package com.call.colorscreen.ledflash.ads

import android.app.Activity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.*

class InterstitialAdsManager {
    private var interstitialAd: InterstitialAd? = null
    private var activity: Activity
    private var ID_ADS = ""
    private var listener: InterstitialAdListener? = null
    var onAdClosed: (() -> Unit)? = null
    var isLoaded = false
        private set
    var isAdLoadFail = false
        private set
    var isShowAds = false
    var isLoading = false

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
                    this@InterstitialAdsManager.interstitialAd = interstitialAd
                    if (listener != null) {
                        listener!!.onAdLoaded(interstitialAd)
                    }
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            isLoaded = false
                            isShowAds = false
                            this@InterstitialAdsManager.interstitialAd = null
                            onAdClosed?.invoke()
                            if (listener != null) {
                                listener!!.onAdDismissedFullScreenContent()
                            }
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            this@InterstitialAdsManager.interstitialAd = null
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

    fun showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd!!.show(activity)
        }
    }
}