package com.call.colorscreen.ledflash.ui.main.themes

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.call.colorscreen.ledflash.database.Theme

class ThemeDiffCallBack : DiffUtil.ItemCallback<Theme>() {
    override fun areItemsTheSame(oldItem: Theme, newItem: Theme): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Theme, newItem: Theme): Boolean {
        return oldItem == newItem
    }
}