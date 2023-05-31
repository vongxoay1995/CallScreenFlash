package com.call.colorscreen.ledflash.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.databinding.ActivityPermissionOverBinding
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.PhoneUtils.context

class PermissionOverActivity : BaseActivity<ActivityPermissionOverBinding>(){
    private var typePromt = 0

    override fun getLayoutId(): Int {
       return R.layout.activity_permission_over
    }

    override fun onViewReady(savedInstance: Bundle?) {
        requestedOrientation = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        else
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        typePromt = intent.getIntExtra(Constant.TYPE_PERMISSION, -1)
        when (typePromt) {
            0 -> {
                binding.txtTitle.text = getString(R.string.titleDrawOver)
                binding.txtPermissionContent.text = getString(R.string.prompt_permission_draw_window_msg)
            }
            1 -> {
                binding.txtTitle.text = getString(R.string.titleNotification)
                binding.txtPermissionContent.text = getString(R.string.prompt_permission_notification_msg)
            }
            else -> {
                finish()
            }
        }
    }
    companion object {
        @JvmStatic
        fun open(context: Context?, typePromt: Int) {
            if (context == null) {
                return
            }
            Handler(Looper.getMainLooper()).post {
                val intent =
                    Intent(
                        context,
                        PermissionOverActivity::class.java
                    )
                intent.putExtra(Constant.TYPE_PERMISSION, typePromt)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreate() {

    }

}