package com.call.colorscreen.ledflash.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.ActivityMainBinding
import com.call.colorscreen.ledflash.model.Data
import com.call.colorscreen.ledflash.repository.RetrofitInstance
import com.call.colorscreen.ledflash.service.ApiService
import com.call.colorscreen.ledflash.ui.main.adapter.ViewPagerAdapter
import com.call.colorscreen.ledflash.ui.main.custom.CustomFragment
import com.call.colorscreen.ledflash.ui.main.themes.EventBusMain
import com.call.colorscreen.ledflash.ui.main.themes.ThemesFragment
import com.call.colorscreen.ledflash.ui.setting.SettingActivity
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.HawkData
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

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onViewReady(savedInstance: Bundle?) {
        initView()
    }

    private fun initView() {
        AppUtil.overHeader(this, binding.layoutHeader)
        listener()
        loadApi(true)
        disableToolTipTextTab()
        initPager()
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
        binding.viewpager.offscreenPageLimit = 2
    }

    private fun listener() {
        binding.btnSetting.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnSetting -> {
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
                if (response.body() != null && response.body()!!.app!!.size > 0) {
                    checkData(response.body()!!.app, response.body()!!.changeLog!!.version)
                }
                val eventBusMain = EventBusMain(true, isRefresh)
                EventBus.getDefault().postSticky(eventBusMain)
            }

            override fun onFailure(call: Call<Data?>, t: Throwable) {
                Log.e("TAN", "onFailure: "+t.message)
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
        if (versionApi > version||HawkData.getListThemes().size<10) {
            Log.e("TAN", "checkData: lan dau", )
            for (i in app.indices) {
                app[i].position= mPosition + i
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
}