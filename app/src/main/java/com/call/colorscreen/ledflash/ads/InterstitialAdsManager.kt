package com.call.colorscreen.ledflash.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.call.colorscreen.ledflash.util.AppAdsId
import com.call.colorscreen.ledflash.util.Constant
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.orhanobut.hawk.Hawk
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
                    Log.e("TAN", "onAdLoaded: ")
                    isLoaded = true
                    isLoading = false
                    loadTime = Date().time
                    this@InterstitialAdsManager.interstitialAd = interstitialAd
                    if (listener != null) {
                        listener!!.onAdLoaded(interstitialAd)
                    }
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            isLoaded = false
                            isShowAds = false
                            this@InterstitialAdsManager.interstitialAd = null
                            Hawk.put(Constant.BEFORE_TIME, System.currentTimeMillis())
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
                    Log.e("TAN", "onAdFailedToLoad: "+loadAdError.message )
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
    /*fun showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd!!.show(activity)
        }
    }*/
    fun showInterstitial(): Boolean {
        Log.e("TAN", "showInterstitial: item "+(System.currentTimeMillis() - Hawk.get(Constant.BEFORE_TIME, 0L)))
        if (System.currentTimeMillis() - Hawk.get(Constant.BEFORE_TIME, 0L) < Hawk.get(Constant.TIME_BETWEEN_ADS, 30000L)) {
            Log.e("TAN", "showInterstitial: disssss")
            return false
        }
        if (interstitialAd != null) {
            interstitialAd!!.show(activity)
            return true
        }
        return false
    }
    companion object{
        @SuppressLint("StaticFieldLeak")
        private var sInterstitial: InterstitialAdsManager?=null
        fun getInstance(activity: Activity): InterstitialAdsManager {
            if (sInterstitial == null) {
                sInterstitial = InterstitialAdsManager(activity, AppAdsId.inter_select_item)
            }
            return sInterstitial as InterstitialAdsManager
        }
    }
}