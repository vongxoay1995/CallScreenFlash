package com.call.colorscreen.ledflash.ui.setting

import android.R.attr.name
import android.R.id
import android.os.Bundle
import android.text.Html
import android.view.View
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.databinding.ActivityTipsBinding
import com.call.colorscreen.ledflash.util.AppUtil


class TipsActivity : BaseActivity<ActivityTipsBinding>(),
    View.OnClickListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_tips
    }

    override fun onViewReady(savedInstance: Bundle?) {
        AppUtil.overHeader(this, binding.layoutHeader)
        binding.btnBack.setOnClickListener(this)
        binding.tips.text =  Html.fromHtml(getString(R.string.faq1))
        binding.tips2.text =  Html.fromHtml(getString(R.string.faq2))
        binding.tips3.text =  Html.fromHtml(getString(R.string.faq3))
    }

    override fun onCreate() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBack -> {
                finish()
            }
        }
    }
}