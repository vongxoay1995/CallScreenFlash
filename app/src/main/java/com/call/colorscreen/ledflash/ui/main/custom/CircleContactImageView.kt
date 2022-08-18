package com.call.colorscreen.ledflash.ui.main.custom

import android.R
import android.content.Context
import android.util.AttributeSet
import android.view.SoundEffectConstants
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView

class CircleContactImageView : AppCompatImageView, Checkable {
    private var isChecked = false

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attributeSet: AttributeSet?) : super(
        context!!, attributeSet
    ) {
    }

    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context!!, attributeSet, i
    ) {
    }

    override fun performClick(): Boolean {
        toggle()
        val performClick = super.performClick()
        if (!performClick) {
            playSoundEffect(SoundEffectConstants.CLICK)
        }
        return performClick
    }

    override fun onCreateDrawableState(i: Int): IntArray {
        val onCreateDrawableState = super.onCreateDrawableState(i + 1)
        if (isChecked()) {
            mergeDrawableStates(onCreateDrawableState, CHECKED_STATE_SET)
        }
        return onCreateDrawableState
    }

    override fun setChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(R.attr.state_checked)
    }
}