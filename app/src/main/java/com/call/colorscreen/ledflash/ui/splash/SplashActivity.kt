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
import com.call.colorscreen.ledflash.databinding.ActivitySplashBinding
import com.call.colorscreen.ledflash.ui.main.MainActivity
import com.call.colorscreen.ledflash.util.AppUtil
import com.google.android.gms.ads.interstitial.InterstitialAd


class SplashActivity : BaseActivity<ActivitySplashBinding>(), View.OnClickListener,
    SplashAppOpenAdsListener {
    var activeScreen = false
    private var timer: CountDownTimer? = null
    private lateinit var splashAppOpenManager: SplashAppOpenManager
    private var analystic: Analystic? = null


    private fun checkAds() {
        if (AppUtil.checkInternet(this)) {
            loadAds()
        } else {
            skip(3)
        }
    }

    private fun loadAds() {
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

    override fun onStop() {
        activeScreen = false
        super.onStop()
    }

    override fun onStart() {
        activeScreen = true
        super.onStart()
    }

    private fun skip(from:Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (binding.seekbar.progress >= 100) {
            skip(1);
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
        analystic = Analystic.getInstance(this)
        analystic?.trackEvent(ManagerEvent.splashShow())
    }

    override fun adShow() {

    }

    override fun adDismiss() {
        skip(6)
    }

    override fun adFailedToShow() {
        Log.e("TAN", "adFailedToShow: ")
    }
}