package com.call.colorscreen.ledflash.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.call.colorscreen.ledflash.BuildConfig
import com.call.colorscreen.ledflash.MyApplication
import com.call.colorscreen.ledflash.ads.TestAds.Companion.instance
import com.call.colorscreen.ledflash.ui.splash.SplashActivity
import com.call.colorscreen.ledflash.util.AppAdsId
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.*

class SplashAppOpenManager(private val application: MyApplication) : LifecycleObserver,
    Application.ActivityLifecycleCallbacks {
    private var appOpenAd: AppOpenAd? = null
    private val id_ads: String? = null
    private var loadCallback: AppOpenAd.AppOpenAdLoadCallback? = null
    private var appOpenAdsListener: SplashAppOpenAdsListener? = null
    var currentActivity: Activity? = null
    private var loadTime: Long = 0
    var isAdLoadFailed = false
    var isAdLoadEnd = false

    fun setAppOpenAdsListener(appOpenAdsListener: SplashAppOpenAdsListener?) {
        this.appOpenAdsListener = appOpenAdsListener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.e("TAN", "onStart: ")
       // if (!AppPreference.getInstance(application.applicationContext).stateBilling) {
            if (currentActivity is SplashActivity/*&&(currentActivity as SplashActivity).isCheckIAPComplete*/) {
                showAdIfAvailable()
            }
       // }
    }

    fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        if (appOpenAdsListener != null) {
                            appOpenAdsListener!!.adDismiss()
                        }
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.e("TAN", "onAdFailedToShowFullScreenContent: "+adError.message)
                        if (appOpenAdsListener != null) {
                            appOpenAdsListener!!.adFailedToShow()
                        }
                        isShowingAd = false
                    }

                    override fun onAdShowedFullScreenContent() {
                        if (appOpenAdsListener != null) {
                            appOpenAdsListener!!.adShow()
                        }
                        isShowingAd = true
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                    }
                }
            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            if (currentActivity is SplashActivity) {
                if ((currentActivity as SplashActivity).activeScreen) {
                    appOpenAd!!.show(currentActivity)
                }
            }
        } else {
            fetchAd()
        }
    }

    fun fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable) {
            return
        }
        isAdLoadFailed = false
        isAdLoadEnd = false
        loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                this@SplashAppOpenManager.appOpenAd = appOpenAd
                loadTime = Date().time
                isAdLoadEnd = true
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.e("TAN", "onAdFailedToLoad: "+loadAdError.message)
                isAdLoadFailed = true
                isAdLoadEnd = true
            }
        }
        val request = adRequest
        if (BuildConfig.DEBUG) {
            instance.generateDeviceId(application)
        }
        AppOpenAd.load(
            application, AppAdsId.idsplash, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    /**
     * Creates and returns ad request.
     */
    private val adRequest: AdRequest
        private get() {
            val builder = AdRequest.Builder()
            return builder.build()
        }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    val isAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    companion object {
        var isShowingAd = false
    }

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
}