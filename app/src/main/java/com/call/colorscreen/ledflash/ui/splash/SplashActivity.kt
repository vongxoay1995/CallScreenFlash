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
import com.call.colorscreen.ledflash.R
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

class SplashActivity : BaseActivity<ActivitySplashBinding>(), View.OnClickListener {
    private var ID_INTER_TEST = "aaca-app-pub-3940256099942544/1033173712"
    private val ID_FB_TEST = "YOUR_PLACEMENT_ID"
    private var mInterstitialAd: InterstitialAd? = null
    private var idAds: String = ""
    private var fullAdsLoaded = false
    private var loadFailed = false
    private var activeScreen = false
    private var loadFBFailed = false
    private var endCountTimer = false
    private var nativeFB: NativeAd? = null
    private var timer: CountDownTimer? = null
    private var adView: LinearLayout? = null
    private fun loadNativeAdFb() {
        val idFB: String = ID_FB_TEST
//        if (BuildConfig.DEBUG) {
//            idFB = ID_FB_TEST
//        } else {
//
//        }
        nativeFB = NativeAd(this, idFB)
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {}
            override fun onError(ad: Ad, adError: com.facebook.ads.AdError) {
                loadFBFailed = true
                Log.e("TAN", "Splash  FB onError: " + adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                if (nativeFB == null || nativeFB !== ad) {
                    return
                }
                Log.e("TAN", "Splash  FB onAdLoaded: ")
            }

            override fun onAdClicked(ad: Ad) {}
            override fun onLoggingImpression(ad: Ad) {}
        }

        // Request an ad
        nativeFB?.loadAd(
            nativeFB?.buildLoadAdConfig()
                ?.withAdListener(nativeAdListener)
                ?.build()
        )
    }

    private fun checkAds() {
        if (AppUtil.checkInternet(this)) {
            loadAds()
            loadNativeAdFb()
        } else {
            skip()
        }
    }

    private fun loadAds() {
        idAds = ID_INTER_TEST
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
            })
        countTimeAds()
    }

    private fun countTimeAds() {
        timer = object : CountDownTimer(6000, 150) {
            override fun onTick(millisUntilFinished: Long) {
                var progress = (100 - (millisUntilFinished.toDouble() / 6000) * 100).toInt()
                if (progress > 98) {
                    binding.seekbar.progress = 100
                } else {
                    binding.seekbar.progress = progress
                }
                countTimer()
                Log.e("TAN", "onTick: " + binding.seekbar.progress + "--" + millisUntilFinished)
            }

            override fun onFinish() {
                Log.e("TAN", "onFinish: ")
            }
        }
        (timer as CountDownTimer).start()
    }

    private fun cancelCountimer() {
        if (timer != null) {
            timer?.cancel()
        }
    }

    private fun countTimer() {
        Log.e("TAN", "countTimer: " + binding.seekbar.progress)
        if (binding.seekbar.progress < 100) {
            if (fullAdsLoaded) {
                showAds()
                hideLoading()
                cancelCountimer()
            } else if (loadFailed) {
                if (loadFBFailed) {
                    hideLoading()
                    endCountTimer = true
                    cancelCountimer()
                    if (activeScreen) {
                        skip()
                    }
                } else if (nativeFB?.isAdLoaded == true) {
                    inflateAd(nativeFB!!)
                    hideSeekbar()
                    cancelCountimer()

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
    }

    private fun hideSeekbar() {
        binding.layoutFooter.visibility = View.VISIBLE
        binding.seekBar.visibility = View.INVISIBLE
    }

    private fun hideLoading() {
        binding.layoutFooter.visibility = View.INVISIBLE
    }

    private fun showAds() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        }
    }

    private fun inflateAd(nativeAdFB: NativeAd) {
        binding.llSkip.visibility = View.VISIBLE
        binding.btnStart.visibility = View.VISIBLE
        nativeAdFB.unregisterView()
        val inflater = LayoutInflater.from(this)
        adView =
            inflater.inflate(R.layout.native_fb_layout, binding.fbNativeAds, false) as LinearLayout
        binding.fbNativeAds.addView(adView)

        // Add the AdOptionsView
        val adChoicesContainer = findViewById<LinearLayout>(R.id.ad_choices_container)
        val adOptionsView = AdOptionsView(this, nativeAdFB, binding.fbNativeAds)
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
        nativeAdTitle.text = nativeAdFB.advertiserName
        nativeAdBody.text = nativeAdFB.adBodyText
        nativeAdSocialContext.text = nativeAdFB.adSocialContext
        nativeAdCallToAction.visibility =
            if (nativeAdFB.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeAdFB.adCallToAction
        sponsoredLabel.text = nativeAdFB.sponsoredTranslation

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeAdFB.registerViewForInteraction(
            adView, nativeAdMedia, nativeAdIcon, clickableViews
        )
    }

    override fun onResume() {
        super.onResume()
        activeScreen = true
    }

    override fun onStop() {
        super.onStop()
        activeScreen = false

    }

    private fun skip() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnStart -> {
                skip()
            }
            R.id.ll_skip -> {
                skip()
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
        Handler().postDelayed(Runnable { skip() }, 3000)
        //checkAds()
        binding.llSkip.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
    }

    override fun onCreate() {

    }
}