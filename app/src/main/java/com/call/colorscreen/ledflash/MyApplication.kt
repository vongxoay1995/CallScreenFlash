package com.call.colorscreen.ledflash

import android.app.Application
import android.util.Log
import com.call.colorscreen.ledflash.di.appModule
import com.call.colorscreen.ledflash.util.AppOpenManager
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.HawkData
import com.call.colorscreen.ledflash.util.PreferencesUtils
import com.facebook.FacebookSdk
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.tasks.Task
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.Executors


class MyApplication : Application() {
   // lateinit var splashAppOpenManager: SplashAppOpenManager
   lateinit var appOpenManager: AppOpenManager
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    override fun onCreate() {
        super.onCreate()
        Log.e("TAN", "onCreate: application")
        MobileAds.initialize(this)
        if (BuildConfig.DEBUG){
            val testDeviceIds: List<String> =
                mutableListOf("55212404B8A86E84C7904DC519111B68")
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
        }
        Hawk.init(this).build()
        appOpenManager = AppOpenManager(this)
       // if (!PreferencesUtils.getBoolean(AppConstant.IS_PURCHASED, false)) {
        //}
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        loadDataFirst()
        setupKoin()
        PreferencesUtils.init(this)
        AudienceNetworkAds.initialize(this)
        FacebookSdk.sdkInitialize(this)
       /* Thread(Runnable {
            configFirebaseRemote()
        }).start()*/
    }
    private fun setupKoin() {
        startKoin {
            androidContext(this@MyApplication)
            modules(
                appModule
            )
        }
    }

    private fun configFirebaseRemote() {
        val cacheExpiration: Long
        cacheExpiration = if (BuildConfig.DEBUG) {
            0
        } else {
            10 // 10 s same as the default value
        }
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(
                cacheExpiration
            ).build()
        mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
        fetchDataFromFirebase()
    }
    private fun fetchDataFromFirebase() {
        mFirebaseRemoteConfig!!.fetch()
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    mFirebaseRemoteConfig!!.activate()
                        .addOnCompleteListener { task12: Task<Boolean?> ->
                            task12.addOnCompleteListener { task1: Task<Boolean?>? -> createAndPostFirebaseEvent() }
                                .addOnCanceledListener { createAndPostFirebaseEvent() }
                        }
                        .addOnCanceledListener { createAndPostFirebaseEvent() }
                        .addOnFailureListener({ e: Exception? -> createAndPostFirebaseEvent() })
                } else {
                    createAndPostFirebaseEvent()
                }
            }.addOnCanceledListener({ createAndPostFirebaseEvent() })
    }

    private fun createAndPostFirebaseEvent() {
        var time = mFirebaseRemoteConfig!!.getLong(Constant.TIME_BETWEEN_ADS)
        if (time<1)
            time=30000
        Hawk.put(Constant.TIME_BETWEEN_ADS, time)
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