package com.call.colorscreen.ledflash.ui.main.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.call.colorscreen.ledflash.base.BaseFragmentt
import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.FragmentCustomBinding
import com.call.colorscreen.ledflash.ui.main.themes.SimpleDividerItemDecoration
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.Constant
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject

class CustomFragment : BaseFragmentt<FragmentCustomBinding>(),CustomThemeAdapter.Listener {
    var adapter: CustomThemeAdapter? = null
    private var pathUriImage: String? = null
    val database by inject<AppDatabase>()
    private var positionItemThemeSelected = -1
    override fun init() {
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        binding.rcvBgMyTheme.layoutManager = gridLayoutManager
        binding.rcvBgMyTheme.itemAnimator = DefaultItemAnimator()
        binding.rcvBgMyTheme.addItemDecoration(SimpleDividerItemDecoration(AppUtil.dpToPx(5)))
        val animator: ItemAnimator = binding.rcvBgMyTheme.itemAnimator!!
        if (animator is SimpleItemAnimator) {
            (animator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        adapter = context?.let { CustomThemeAdapter(it,database) }
        adapter!!.setListenerAdapter(this)
        binding.rcvBgMyTheme.adapter = adapter
        binding.rcvBgMyTheme.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 0) {
                    adapter!!.reload()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            pathUriImage = savedInstanceState.getString(Constant.CAPTURE_IMAGE_PATH)
        }
       /* if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }*/
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constant.CAPTURE_IMAGE_PATH, pathUriImage)

    }

    override fun onAdd() {

    }

    override fun onItemThemeSelected(position: Int) {
    }

    override fun onItemClick(
        backgrounds: ArrayList<Theme>?,
        position: Int,
        delete: Boolean,
        posRandom: Int
    ) {
    }
}