package com.call.colorscreen.ledflash.ads

import com.google.android.gms.ads.LoadAdError

abstract class BannerAdsListener {
    open fun onAdFailedToLoad(loadAdError: LoadAdError?) {}
    open fun onAdLoaded() {}
    open fun onAdClicked() {}
    open fun onAdClosed() {}
}