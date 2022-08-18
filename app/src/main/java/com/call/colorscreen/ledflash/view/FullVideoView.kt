package com.call.colorscreen.ledflash.view

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class FullVideoView(context: Context?, attrs: AttributeSet?) :
    VideoView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize(0, widthMeasureSpec),
            getDefaultSize(0, heightMeasureSpec)
        )
    }
}