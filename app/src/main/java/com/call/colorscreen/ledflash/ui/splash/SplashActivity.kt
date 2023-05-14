package com.call.colorscreen.ledflash.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.MyApplication
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.ads.SplashAppOpenAdsListener
import com.call.colorscreen.ledflash.ads.SplashAppOpenManager
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.databinding.ActivitySplashBinding
import com.call.colorscreen.ledflash.ui.main.MainActivity
import com.call.colorscreen.ledflash.util.AppUtil
import com.facebook.ads.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.*

class SplashActivity : BaseActivity<ActivitySplashBinding>(), View.OnClickListener,
    SplashAppOpenAdsListener {
    private var ID_INTER_TEST = "ca-app-pub-3940256099942544/1033173712"
    private var mInterstitialAd: InterstitialAd? = null
    private var idAds: String = ""
    private var fullAdsLoaded = false
    private var loadFailed = false
    var activeScreen = false
    private var endCountTimer = false
    private var timer: CountDownTimer? = null
    private lateinit var splashAppOpenManager: SplashAppOpenManager
    private fun checkAds() {
        if (AppUtil.checkInternet(this)) {
            loadAds()
        } else {
            skip(3)
        }
    }

    private fun loadAds() {
       /* idAds = ID_INTER_TEST
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, idAds, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    fullAdsLoaded = true
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                skip()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                skip()
                            }

                            override fun onAdShowedFullScreenContent() {
                                mInterstitialAd = null
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    loadFailed = true
                    fullAdsLoaded = false
                    mInterstitialAd = null
                }
            })*/
        splashAppOpenManager.fetchAd()
        countTimeAds()
    }

    private fun countTimeAds() {
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
                skip(2)
                Log.e("TAN", "onFinish: ")
            }
        }
        (timer as CountDownTimer).start()
        splashAppOpenManager.setAppOpenAdsListener(this)
    }

    private fun cancelCountimer() {
        if (timer != null) {
            timer?.cancel()
        }
    }

 /*   private fun countTimer() {
        Log.e("TAN", "countTimer: " + binding.seekbar.progress)
        if (binding.seekbar.progress < 100) {
            if (fullAdsLoaded) {
                showAds()
                hideLoading()
                cancelCountimer()
            } else if (loadFailed) {
                hideLoading()
                endCountTimer = true
                cancelCountimer()
                if (activeScreen) {
                    skip()
                }
            }
        } else {
            hideLoading()
            endCountTimer = true
            cancelCountimer()
            if (activeScreen) {
                skip()
            }
        }
    }*/

    private fun hideLoading() {
        binding.layoutFooter.visibility = View.INVISIBLE
    }

    private fun showAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        }
    }

    override fun onResume() {
        super.onResume()
        activeScreen = true
    }

    override fun onStop() {
        super.onStop()
        activeScreen = false

    }

    private fun skip(from:Int) {
        Log.e("TAN", "skip: "+from)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (binding.seekbar.progress >= 100) {
            skip(1)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnStart -> {
                skip(4)
            }
            R.id.ll_skip -> {
                skip(5)
            }
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
        splashAppOpenManager = (application as MyApplication).splashAppOpenManager
    }

    override fun adShow() {

    }

    override fun adDismiss() {
        skip(6)
    }

    override fun adFailedToShow() {
    }
}