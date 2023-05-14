package com.call.colorscreen.ledflash

import android.app.Application
import android.util.Log
import com.call.colorscreen.ledflash.ads.SplashAppOpenManager
import com.call.colorscreen.ledflash.database.RoomManager
import com.call.colorscreen.ledflash.di.appModule
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.HawkData
import com.facebook.ads.AdSettings
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.koin.androidContext
import java.util.*
import java.util.concurrent.Executors
import org.koin.core.context.startKoin
class MyApplication : Application() {
    lateinit var splashAppOpenManager: SplashAppOpenManager

    override fun onCreate() {
        super.onCreate()
        Log.e("TAN", "onCreate: application")
        MobileAds.initialize(this)
        Hawk.init(this).build()
        RoomManager.create(this)
        if(BuildConfig.DEBUG){
            val testDeviceIds = listOf("C672C9D51F65E8B9B0345F9F8E4F7CC1")
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
            AdSettings.addTestDevice("d9db6482-fb0e-4df0-81ca-d47544569596")
        }
        loadDataFirst()
        setupKoin()
        splashAppOpenManager = SplashAppOpenManager(this)

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