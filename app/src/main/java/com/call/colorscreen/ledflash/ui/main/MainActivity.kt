package com.call.colorscreen.ledflash.ui.main

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.call.colorscreen.ledflash.MyApplication
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.ads.BannerAdsUtils
import com.call.colorscreen.ledflash.ads.InterstitialAdsManager
import com.call.colorscreen.ledflash.ads.InterstitialApply
import com.call.colorscreen.ledflash.analystic.Analystic
import com.call.colorscreen.ledflash.analystic.ManagerEvent
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.ActivityMainBinding
import com.call.colorscreen.ledflash.databinding.LayoutBottomSheetRateBinding
import com.call.colorscreen.ledflash.model.Data
import com.call.colorscreen.ledflash.repository.RetrofitInstance
import com.call.colorscreen.ledflash.service.ApiService
import com.call.colorscreen.ledflash.service.PhoneStateService
import com.call.colorscreen.ledflash.ui.main.adapter.ViewPagerAdapter
import com.call.colorscreen.ledflash.ui.main.custom.CustomFragment
import com.call.colorscreen.ledflash.ui.main.themes.EventBusMain
import com.call.colorscreen.ledflash.ui.main.themes.ThemesFragment
import com.call.colorscreen.ledflash.ui.setting.SettingActivity
import com.call.colorscreen.ledflash.util.AppAdsId
import com.call.colorscreen.ledflash.util.AppOpenManager
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.Constant.MAIL_LIST
import com.call.colorscreen.ledflash.util.Constant.PLAY_STORE_LINK
import com.call.colorscreen.ledflash.util.Constant.RATE_FEED_BACK
import com.call.colorscreen.ledflash.util.Constant.RATE_IN_APP
import com.call.colorscreen.ledflash.util.Constant.RATE_LATER
import com.call.colorscreen.ledflash.util.HawkData
import com.call.colorscreen.ledflash.view.AnimationRatingBar
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import isActive
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/*-cách 2: đệ quy
tìm kiếm phần tử
#include <bits/stdc++.h>
using namespace std;
int search(int a[],int l,int r,int x)
{
    if(l>r)
        return -1;
    int m=(l+r)/2;
    if(a[m]==x)
        return m;
    else if(x<a[m])
        return search(a,l,m-1,x);
    else
        return search(a,m+1,r,x);

}*/
class MainActivity : BaseActivity<ActivityMainBinding>(),  AppOpenManager.AppOpenManagerObserver,View.OnClickListener {
    private var adapter: ViewPagerAdapter? = null
    private var themesFragment: ThemesFragment? = null
    private var customFragment: CustomFragment? = null
    private lateinit var bannerAdsUtils: BannerAdsUtils
    private lateinit var analystic: Analystic
    var bottomSheetDialog: BottomSheetDialog? = null
    var TYPE_RATE = RATE_LATER
    var numberExitAllowShowRate = arrayOf(1,4,7)
    lateinit var interApply: InterstitialApply
    lateinit var interTheme: InterstitialAdsManager
    private lateinit var appOpenManager: AppOpenManager

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onViewReady(savedInstance: Bundle?) {
        initView()
    }

    private fun initView() {
        if (HawkData.getEnableCall()) {
            PhoneStateService.startService(this)
        }
        analystic = Analystic.getInstance(this)
        analystic.trackEvent(ManagerEvent.mainShow())
        AppUtil.overHeader(this, binding.layoutHeader)
        listener()
        loadApi(true)
        if (AppUtil.checkInternet(this)) {
            loadAds()
        } else {
            binding.llAds.visibility = View.GONE;
        }
        disableToolTipTextTab()
        initPager()
        appOpenManager = (application as MyApplication).appOpenManager
        //appOpenManager.fetchAd()
        requestNotificationPermission()

    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean? -> }


    private fun loadAds() {
        bannerAdsUtils = BannerAdsUtils(this, AppAdsId.id_banner_main, binding.llAds)
        bannerAdsUtils.loadAds()
        bannerAdsUtils.goneWhenFail = false
        loadCacheInter()
    }

    override fun onStart() {
        super.onStart()
        appOpenManager.registerObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        appOpenManager.unregisterObserver()
    }
    private fun loadCacheInter() {
        interApply =InterstitialApply.getInstance(this)
        interTheme =InterstitialAdsManager.getInstance(this)
        if (!interApply.isLoading) {
           // interApply.loadAds()
        }
        if (!interTheme.isLoading) {
            interTheme.loadAds()
        }
    }

    private fun initPager() {
        adapter = ViewPagerAdapter(supportFragmentManager)
        themesFragment = ThemesFragment()
        customFragment = CustomFragment()
        adapter!!.addFragment(themesFragment, getString(R.string.themesFragmentTitle))
        adapter!!.addFragment(customFragment, getString(R.string.customFragmentTitle))
        binding.viewpager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewpager)
        binding.viewpager.currentItem = 0
        binding.viewpager.offscreenPageLimit = 1
    }

    private fun listener() {
        binding.btnSetting.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSetting -> {
                analystic.trackEvent(ManagerEvent.mainSettingClick())
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
    }

    fun refreshApi() {
        loadApi(false)
    }

    private fun disableToolTipTextTab() {
        val tabStrip = binding.tabs.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnLongClickListener { v: View? -> true }
        }
    }

    lateinit var mCurrentName: MutableLiveData<Boolean>
    fun getCurrentName(): MutableLiveData<Boolean> {
        if (mCurrentName == null) {
            mCurrentName = MutableLiveData<Boolean>()
        }
        return mCurrentName
    }

    private fun loadApi(isRefresh: Boolean) {
        val apiService: ApiService = RetrofitInstance.api
        val app: Call<Data> = apiService.getThemes()
        app.enqueue(object : Callback<Data?> {
            override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                Log.e("TAN", "onResponse: success")
                if (response.body() != null && response.body()!!.app.size > 0) {
                    checkData(response.body()!!.app, response.body()!!.changeLog!!.version)
                }
                val eventBusMain = EventBusMain(true, isRefresh)
                EventBus.getDefault().postSticky(eventBusMain)
            }

            override fun onFailure(call: Call<Data?>, t: Throwable) {
                Log.e("TAN", "onFailure: " + t.message)
                val eventBusMain = EventBusMain(true, isRefresh)
                EventBus.getDefault().postSticky(eventBusMain)
            }
        })
    }

    private fun checkData(app: MutableList<Theme>, versionApi: Int) {
        var arrList: MutableList<Theme> = mutableListOf()
        val version: Int = HawkData.getVersion()
        val arr: MutableList<Theme> = HawkData.getListThemes()

        val mPosition: Int = HawkData.getListThemes().size
        if (versionApi > version || HawkData.getListThemes().size < 10) {
            Log.e("TAN", "checkData: lan dau")
            for (i in app.indices) {
                app[i].position = mPosition + i
                arr.add(app[i])
            }
            HawkData.setVersion(versionApi)
            HawkData.setListThemes(arr)
        }

        /*if (versionApi > version||HawkData.getListThemes().size<10) {
            HawkData.setListThemes(arrList)
            arrList.addAll(HawkData.getListThemesDefault())
            arrList.addAll(app)
            HawkData.setListThemes(arrList)
            HawkData.setVersion(versionApi)
            Log.e("TAN", "checkData: " + HawkData.getListThemes().size)
        }*/
    }

    override fun onCreate() {

    }

    fun initBottomSheetRate() {
        bottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
        val bottomBinding: LayoutBottomSheetRateBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.layout_bottom_sheet_rate, null, false)
        bottomSheetDialog!!.setContentView(bottomBinding.root)
        bottomSheetDialog!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog!!.show()
        analystic.trackEvent(ManagerEvent.rateShow())
        bottomBinding.tvRate.setOnClickListener {
            analystic.trackEvent(ManagerEvent.rateClick())
            when (TYPE_RATE) {
                RATE_IN_APP -> {
                    analystic.trackEvent(ManagerEvent.rateInApp())
                    rateInApp()
                }
                RATE_FEED_BACK -> {
                    analystic.trackEvent(ManagerEvent.rateSendFeedBack())
                    sendFeedBack()
                }
            }
            bottomSheetDialog!!.dismiss()
        }
        bottomSheetDialog!!.setOnDismissListener {
            analystic.trackEvent(ManagerEvent.rateDismiss())
        }
        Handler().postDelayed({
            bottomBinding.ratingbar.setRating(5, true)
        }, 500)
        bottomBinding.ratingbar.setListener(object : AnimationRatingBar.Listener {
            override fun getRate(rate: Int) {
                when (rate) {
                    5 -> {
                        TYPE_RATE = RATE_IN_APP
                        bottomBinding.icRate.setImageDrawable(
                            ContextCompat
                            .getDrawable(baseContext, R.drawable.image_rate_happy))
                        bottomBinding.tvRate.text = getString(R.string.rate_on_gg_play)
                        bottomBinding.tvContentRate.text = getString(R.string.rate_title3)
                        bottomBinding.tvGuideRate.text = getString(R.string.rate_content3)
                        bottomBinding.tvRate.setTextColor(ContextCompat.getColor(baseContext, R.color.white))
                        bottomBinding.tvRate.background =
                            ContextCompat.getDrawable(baseContext, R.drawable.bg_button_rate)
                    }
                    0 -> {
                        TYPE_RATE = RATE_LATER
                    }
                    else -> {
                        TYPE_RATE = RATE_FEED_BACK
                        bottomBinding.icRate.setImageDrawable(ContextCompat
                            .getDrawable(baseContext, R.drawable.image_rate_sad))
                        bottomBinding.tvRate.text = getString(R.string.feed_back_rate)
                        bottomBinding.tvContentRate.text = getString(R.string.rate_title2)
                        bottomBinding.tvGuideRate.text = getString(R.string.rate_content2)
                        bottomBinding.tvRate.setTextColor(ContextCompat.getColor(baseContext, R.color.white))
                        bottomBinding.tvRate.background =
                            ContextCompat.getDrawable(baseContext, R.drawable.bg_button_rate)
                    }
                }
            }
        })
    }



    private fun sendFeedBack() {
        val mailSubject = getString(R.string.mail_subject)
        val mailContent = ""
        val mailIntent = Intent(Intent.ACTION_SEND)
        mailIntent.type = "text/email"
        mailIntent.putExtra(Intent.EXTRA_EMAIL, MAIL_LIST)
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject)
        mailIntent.putExtra(Intent.EXTRA_TEXT, mailContent)
        if (packageManager.getLaunchIntentForPackage("com.google.android.gm") != null) {
            mailIntent.setPackage("com.google.android.gm")
        }
        startActivity(Intent.createChooser(mailIntent, "$mailSubject:"))
    }

    fun rateInApp() {
        val reviewManager: ReviewManager = ReviewManagerFactory.create(this)
        val request: com.google.android.play.core.tasks.Task<ReviewInfo> =
            reviewManager.requestReviewFlow()
        request.addOnSuccessListener { result ->
            val flow: com.google.android.play.core.tasks.Task<Void> =
                reviewManager.launchReviewFlow(this, result)
            flow.addOnSuccessListener {
                HawkData.setRate(true)
            }
        }.addOnFailureListener { e -> goToStoreApp() }
    }

    private fun goToStoreApp() {
        analystic.trackEvent(ManagerEvent.rateGotoStore())
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val linkRateApp: String = PLAY_STORE_LINK + packageName
            intent.data = Uri.parse(linkRateApp)
            startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
            anfe.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (!HawkData.isAllowRate()){
            super.onBackPressed()
        }else{
            var countExitApp = HawkData.getCountExitApp()
            countExitApp++
            HawkData.setExitApp(countExitApp)
            if (numberExitAllowShowRate.contains(countExitApp) && !HawkData.isRated()!!) {
                initBottomSheetRate()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun lifecycleStart(appOpenAd: AppOpenAd, appOpenManager: AppOpenManager) {
        if (isActive() && !interTheme.isShowAdsInter() && customFragment != null && !(customFragment as CustomFragment).isRequestImageVideo) {
            appOpenAd.show(this)
        }
    }

    override fun lifecycleShowAd() {

    }

    override fun lifecycleStop() {

    }
}