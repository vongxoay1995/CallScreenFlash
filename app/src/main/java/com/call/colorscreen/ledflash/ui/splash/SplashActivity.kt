package com.call.colorscreen.ledflash.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.MyApplication
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.ads.SplashAppOpenAdsListener
import com.call.colorscreen.ledflash.ads.SplashAppOpenManager
import com.call.colorscreen.ledflash.analystic.Analystic
import com.call.colorscreen.ledflash.analystic.ManagerEvent
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.ActivitySplashBinding
import com.call.colorscreen.ledflash.ui.main.MainActivity
import com.call.colorscreen.ledflash.util.AppAdsId
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.HawkData
import com.call.colorscreen.ledflash.util.JobScreen
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import isActive
import kotlinx.coroutines.NonCancellable.isActive
import java.util.*


class SplashActivity : BaseActivity<ActivitySplashBinding>(), View.OnClickListener,
    SplashAppOpenAdsListener, JobScreen.JobProgress {
    var activeScreen = false
   // private var timer: CountDownTimer? = null
   // private lateinit var splashAppOpenManager: SplashAppOpenManager
    private var analystic: Analystic? = null
    private lateinit var jobScreen: JobScreen
    private var appOpenAd: AppOpenAd? = null
    private var isLoadAdError = false
    private var isShowAds = false
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var loadedRemoteConfig = false
    private var allowRate = true


    private fun checkAds() {
        if (AppUtil.checkInternet(this)) {
            loadAds()
        } else {
            moveMain(1)
        }
    }

    private fun loadAds() {
        jobScreen.startJob(this)
        //splashAppOpenManager.fetchAd()
        //countTimeAds()
    }

    /*private fun countTimeAds() {
        timer = object : CountDownTimer(8000L, 56L) {
            override fun onTick(millisUntilFinished: Long) {
                binding.seekbar.progress++
                if (binding.seekbar.progress < 100) {
                    if (splashAppOpenManager.isAdLoadEnd) {
                        if (!splashAppOpenManager.isAdLoadFailed) {
                            if (splashAppOpenManager.isAdAvailable) {
                                splashAppOpenManager.showAdIfAvailable()
                                cancelCountimer()
                                binding.seekbar.progress = 100
                                binding.layoutFooter.visibility = View.GONE
                            }
                        } else {
                            cancelCountimer()
                            onFinish()
                        }
                    }
                }
            }

            override fun onFinish() {
                binding.seekbar.progress = 100
                moveMain(2)
                Log.e("TAN", "onFinish: ")
            }
        }
        (timer as CountDownTimer).start()
        splashAppOpenManager.setAppOpenAdsListener(this)
    }*/

  /*  private fun cancelCountimer() {
        if (timer != null) {
            timer?.cancel()
        }
    }*/

    override fun onStop() {
        activeScreen = false
        super.onStop()
    }

    override fun onStart() {
        activeScreen = true
        super.onStart()
    }

    private fun moveMain(from:Int) {
        Log.e("TAN", "moveMain: "+from)
        if (isActive()) {
            stopJobScreen()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun stopJobScreen() {
        if (this::jobScreen.isInitialized) {
            jobScreen.stopJob()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnStart -> {
                moveMain(3)
            }
            R.id.ll_skip -> {
                moveMain(4)
            }
        }
    }
    private fun initRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        fetch()
    }

    private fun fetch() {
        mFirebaseRemoteConfig!!.fetchAndActivate()
            .addOnCompleteListener { task: Task<Boolean> ->
                if (task.isSuccessful && task.result) {
                    loadedRemoteConfig = true
                    allowRate = mFirebaseRemoteConfig!!.getBoolean("rate")
                    HawkData.setAllowRate(allowRate)
                } else {
                    if (task.isSuccessful) {
                        loadedRemoteConfig = true
                    }
                }
            }
            .addOnFailureListener {
                loadedRemoteConfig = true
            }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun onViewReady(savedInstance: Bundle?) {
        if (!isTaskRoot) {
            finish()
            return
        }
        Glide.with(this)
            .load(R.drawable.ic_splash)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .thumbnail(0.2f)
            .into(binding.imgSplash)
        checkAds()
        binding.llSkip.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
    }

    override fun onCreate() {
       // splashAppOpenManager = (application as MyApplication).splashAppOpenManager
        analystic = Analystic.getInstance(this)
        analystic?.trackEvent(ManagerEvent.splashShow())
        val build = AdRequest.Builder().build()
        AppOpenAd.load(
            this, AppAdsId.idsplash, build,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
        jobScreen = JobScreen()
        initRemoteConfig()
        if(HawkData.getThemeSelect().name == "default_3" || HawkData.getThemeSelect().name == "default_4"){
            val bg = Theme(
                0,
                0,
                "thumb/default_1.webp",
                "/raw/default_1",
                false,
                "default_1"
            )
            HawkData.setThemeSelect(bg)
        }
    }

    override fun adShow() {

    }

    override fun adDismiss() {
    }

    override fun adFailedToShow() {
        Log.e("TAN", "adFailedToShow: ")
    }

    private val loadCallback: AppOpenAd.AppOpenAdLoadCallback = object :
        AppOpenAd.AppOpenAdLoadCallback() {

        override fun onAdLoaded(ad: AppOpenAd) {
            if (isActive()) {
                isLoadAdError = false
                ad.fullScreenContentCallback = fullScreenContentCallback
                appOpenAd = ad
            }
        }

        override fun onAdFailedToLoad(p0: LoadAdError) {
            if (isActive()) {
                isLoadAdError = true
            }
        }
    }
    private val fullScreenContentCallback: FullScreenContentCallback = object :
        FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            isShowAds = false
            moveMain(5)
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            isShowAds = false
            moveMain(6)
        }

        override fun onAdShowedFullScreenContent() {
            isShowAds = true
        }
    }

    override fun onDestroy() {
        stopJobScreen()
        super.onDestroy()
    }

    override fun onResume() {
        Log.e("TAN", "onResume: ", )
        if (this::jobScreen.isInitialized) {
            jobScreen.startJob(this)
        }
        super.onResume()
    }

    override fun onPause() {
        Log.e("TAN", "onPause: ", )
        stopJobScreen()
        super.onPause()
    }

    override fun onProgress(count: Int) {
        if (!isActive() || isShowAds) {
            return
        }
        binding.seekbar.progress++
        if (appOpenAd != null) {
            stopJobScreen()
            isShowAds = true
            appOpenAd!!.show(this)
            binding.layoutFooter.visibility = View.GONE
        } else if ((!isShowAds && jobScreen.isProgressMax()) || isLoadAdError) {
            moveMain(7)
        }
    }
}