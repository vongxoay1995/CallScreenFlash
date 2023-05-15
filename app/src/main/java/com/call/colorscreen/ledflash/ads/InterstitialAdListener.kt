package com.call.colorscreen.ledflash.ads

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd

abstract class InterstitialAdListener {
    open fun onAdLoaded(interstitialAd: InterstitialAd?) {}
    open fun onAdFailedToLoad(loadAdError: LoadAdError) {}
    open fun onAdDismissedFullScreenContent() {}
    open fun onAdFailedToShowFullScreenContent(adError: AdError?) {}
    open fun onAdShowedFullScreenContent() {}
}