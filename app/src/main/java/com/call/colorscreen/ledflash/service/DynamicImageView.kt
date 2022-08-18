package com.call.colorscreen.ledflash.service

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.ceil

class DynamicImageView(context: Context?, attrs: AttributeSet?) :
    AppCompatImageView(context!!, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = this.drawable
        if (d != null) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height =
                ceil((width * d.intrinsicHeight.toFloat() / d.intrinsicWidth).toDouble())
                    .toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}