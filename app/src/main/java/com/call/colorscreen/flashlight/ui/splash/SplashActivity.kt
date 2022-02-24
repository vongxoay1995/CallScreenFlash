package com.call.colorscreen.flashlight.ui.splash

import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.flashlight.R
import com.call.colorscreen.flashlight.base.BaseActivity
import com.call.colorscreen.flashlight.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<SplashActivityViewModel,ActivitySplashBinding>(SplashActivityViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            finish()
            return
        }
        setContentView(R.layout.activity_splash)
    }

    override fun initViewModel(viewModel: SplashActivityViewModel) {
       // binding.viewModel = viewModel
    }

    override fun getLayoutRes(): Int {
        return  R.layout.activity_splash
    }
}