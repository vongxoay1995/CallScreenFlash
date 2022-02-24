package com.call.colorscreen.flashlight

import android.app.Application
import android.util.Log
import com.facebook.FacebookSdk
import com.facebook.ads.AudienceNetworkAds
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.MobileAds
import com.orhanobut.hawk.Hawk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.e("TAN", "onCreate: application" )
        MobileAds.initialize(this)
        Hawk.init(this).build()
        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this)
        AudienceNetworkAds.initialize(this)
    }
}