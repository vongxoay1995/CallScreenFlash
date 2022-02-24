package com.call.colorscreen.flashlight.ui.splash

import android.app.Application
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.call.colorscreen.flashlight.MyApplication
import com.call.colorscreen.flashlight.R
import com.call.colorscreen.flashlight.base.BaseViewModel


class SplashActivityViewModel(app: Application) : BaseViewModel(app){
    /*@BindingAdapter("imgSrc")
    fun setImage(imgView: ImageView, imgSrc: Drawable?){
        imgSrc?.let {
            Glide.with(imgView.context)
                    .load(imgSrc)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .thumbnail(0.2f)
                    .into(imgView)
        }
    }*/
}