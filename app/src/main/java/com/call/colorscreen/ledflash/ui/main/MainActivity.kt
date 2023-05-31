package com.call.colorscreen.ledflash.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.call.colorscreen.ledflash.BuildConfig
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.ads.BannerAdsListener
import com.call.colorscreen.ledflash.ads.BannerAdsUtils
import com.call.colorscreen.ledflash.ads.InterstitialAdsManager
import com.call.colorscreen.ledflash.analystic.Analystic
import com.call.colorscreen.ledflash.analystic.ManagerEvent
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.ActivityMainBinding
import com.call.colorscreen.ledflash.databinding.LayoutBottomSheetRateBinding
import com.call.colorscreen.ledflash.model.Data
import com.call.colorscreen.ledflash.repository.RetrofitInstance
import com.call.colorscreen.ledflash.service.ApiService
import com.call.colorscreen.ledflash.ui.main.adapter.ViewPagerAdapter
import com.call.colorscreen.ledflash.ui.main.custom.CustomFragment
import com.call.colorscreen.ledflash.ui.main.themes.EventBusMain
import com.call.colorscreen.ledflash.ui.main.themes.ThemesFragment
import com.call.colorscreen.ledflash.ui.setting.SettingActivity
import com.call.colorscreen.ledflash.util.*
import com.call.colorscreen.ledflash.util.Constant.MAIL_LIST
import com.call.colorscreen.ledflash.util.Constant.PLAY_STORE_LINK
import com.call.colorscreen.ledflash.util.Constant.RATE_FEED_BACK
import com.call.colorscreen.ledflash.util.Constant.RATE_IN_APP
import com.call.colorscreen.ledflash.util.Constant.RATE_LATER
import com.call.colorscreen.ledflash.view.AnimationRatingBar
import com.google.android.gms.ads.LoadAdError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


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
class MainActivity : BaseActivity<ActivityMainBinding>(), View.OnClickListener {
    private var adapter: ViewPagerAdapter? = null
    private var themesFragment: ThemesFragment? = null
    private var customFragment: CustomFragment? = null
    private lateinit var bannerAdsUtils: BannerAdsUtils
    private lateinit var analystic: Analystic
    var bottomSheetDialog: BottomSheetDialog? = null
    var TYPE_RATE = RATE_LATER
    var numberExitAllowShowRate = arrayOf(1,4,7)

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onViewReady(savedInstance: Bundle?) {
        initView()
    }

    private fun initView() {
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
    }

    private fun loadAds() {
        bannerAdsUtils = BannerAdsUtils(this, AppAdsId.id_banner_main, binding.llAds)
        bannerAdsUtils.loadAds()
        bannerAdsUtils.goneWhenFail = false
        bannerAdsUtils.setAdsListener(object : BannerAdsListener() {

            override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                super.onAdFailedToLoad(loadAdError)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
            }

            override fun onAdClicked() {
                super.onAdClicked()
            }
        })
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
        val bottombinding: LayoutBottomSheetRateBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.layout_bottom_sheet_rate, null, false)
        bottomSheetDialog!!.setContentView(bottombinding.root)
        bottomSheetDialog!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog!!.show()
        var backPressToDismiss = true
        bottombinding.tvRate.setOnClickListener {
            backPressToDismiss = false
            when (TYPE_RATE) {
                RATE_IN_APP -> {
                    rateInApp()
                }
                RATE_FEED_BACK -> {
                    sendFeedBack()
                }
            }
            bottomSheetDialog!!.dismiss()
        }
        bottomSheetDialog!!.setOnDismissListener {
            if (backPressToDismiss) {
            }
        }
        Handler().postDelayed({
            bottombinding.ratingbar.setRating(5, true)
        }, 500)
        bottombinding.ratingbar.setListener(object : AnimationRatingBar.Listener {
            override fun getRate(rate: Int) {
                when (rate) {
                    5 -> {
                        TYPE_RATE = RATE_IN_APP
                        bottombinding.icRate.setImageDrawable(resources.getDrawable(R.drawable.image_rate_happy))
                        bottombinding.tvRate.text = getString(R.string.rate_on_gg_play)
                        bottombinding.tvContentRate.text = getString(R.string.rate_title3)
                        bottombinding.tvGuideRate.text = getString(R.string.rate_content3)
                        bottombinding.tvRate.setTextColor(resources.getColor(R.color.white))
                        bottombinding.tvRate.background =
                            resources.getDrawable(R.drawable.bg_button_rate)
                    }
                    0 -> {
                        TYPE_RATE = RATE_LATER
                    }
                    else -> {
                        TYPE_RATE = RATE_FEED_BACK
                        bottombinding.icRate.setImageDrawable(resources.getDrawable(R.drawable.image_rate_sad))
                        bottombinding.tvRate.text = getString(R.string.feed_back_rate)
                        bottombinding.tvContentRate.text = getString(R.string.rate_title2)
                        bottombinding.tvGuideRate.text = getString(R.string.rate_content2)
                        bottombinding.tvRate.setTextColor(resources.getColor(R.color.white))
                        bottombinding.tvRate.background =
                            resources.getDrawable(R.drawable.bg_button_rate)
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