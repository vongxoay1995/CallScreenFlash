package com.call.colorscreen.ledflash

import android.app.Application
import android.util.Log
import com.call.colorscreen.ledflash.ads.SplashAppOpenManager
import com.call.colorscreen.ledflash.database.RoomManager
import com.call.colorscreen.ledflash.di.appModule
import com.call.colorscreen.ledflash.util.AppOpenManager
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.HawkData
import com.call.colorscreen.ledflash.util.PreferencesUtils
import com.facebook.FacebookSdk
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.*
import java.util.concurrent.Executors

class MyApplication : Application() {
   // lateinit var splashAppOpenManager: SplashAppOpenManager
   lateinit var appOpenManager: AppOpenManager

    override fun onCreate() {
        super.onCreate()
        Log.e("TAN", "onCreate: application")
        MobileAds.initialize(this)
        Hawk.init(this).build()
        if(BuildConfig.DEBUG){
            val testDeviceIds = listOf("C672C9D51F65E8B9B0345F9F8E4F7CC1")
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
            AdSettings.addTestDevice("d9db6482-fb0e-4df0-81ca-d47544569596")
        }
        appOpenManager = AppOpenManager(this)
       // if (!PreferencesUtils.getBoolean(AppConstant.IS_PURCHASED, false)) {
        appOpenManager.fetchAd()
        //}
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        loadDataFirst()
        setupKoin()
       // splashAppOpenManager = SplashAppOpenManager(this)
        PreferencesUtils.init(this)
        AudienceNetworkAds.initialize(this)
        FacebookSdk.sdkInitialize(this)
        //FacebookSdk.sdkInitialize(this)
        //AppEventsLogger.activateApp(this)
       // AudienceNetworkAds.initialize(this)
    }
    private fun setupKoin() {
        startKoin {
            androidContext(this@MyApplication)
            modules(
                appModule
            )
        }
    }
    private fun loadDataFirst() {
        if(!HawkData.isFirstData()){
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                AppUtil.loadDataDefault(applicationContext, "thumb")?.let {
                    HawkData.setListThemes(it)
                    HawkData.setListThemesDefault(it)
                }
                HawkData.setFirstData(true)
            }
        }
    }
}