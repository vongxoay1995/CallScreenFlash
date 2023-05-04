package com.call.colorscreen.ledflash.ui.main.themes

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SimpleDividerItem(private val horiZonSpaceHeight: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = horiZonSpaceHeight/2
        //outRect.right = horiZonSpaceHeight
    }
}