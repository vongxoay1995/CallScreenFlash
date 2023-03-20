package com.call.colorscreen.ledflash.ui.main.themes

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.base.BaseFragmentt
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.FragmentThemesBinding
import com.call.colorscreen.ledflash.model.EBApplyTheme
import com.call.colorscreen.ledflash.ui.aply.ApplyActivity
import com.call.colorscreen.ledflash.ui.main.MainActivity
import com.call.colorscreen.ledflash.util.*
import com.call.colorscreen.ledflash.util.Constant.IS_DELETE
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ThemesFragment : BaseFragmentt<FragmentThemesBinding>(),
        NetworkChangeReceiver.Listener, ThemesAdapter.Listener {
    private var networkChangeReceiver: NetworkChangeReceiver? = null
    private lateinit var listThemes: MutableList<Theme>
    private lateinit var adapter: ThemesAdapter
    private var posDownload = -1

    override fun init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        networkChangeReceiver = NetworkChangeReceiver()
        networkChangeReceiver!!.registerReceiver(this.context, this)
        binding.swRefeshLayout.isRefreshing = false
        binding.swRefeshLayout.setOnRefreshListener { this.onRefreshLayout() }
        listThemes = HawkData.getListThemes()
        adapter = context?.let { ThemesAdapter(it, listThemes) }!!
        adapter.setListenerClick(this)
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        binding.rcvThemes.layoutManager = gridLayoutManager
        binding.rcvThemes.itemAnimator = DefaultItemAnimator()
        binding.rcvThemes.addItemDecoration(SimpleDividerItemDecoration(AppUtil.dpToPx(5)))
        val animator: ItemAnimator = binding.rcvThemes.itemAnimator!!
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        binding.rcvThemes.adapter = adapter
        binding.rcvThemes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!context?.let { AppUtil.checkInternet(it) }!!) {
                        Toast.makeText(context, getString(R.string.err_network), Toast.LENGTH_SHORT)
                                .show()
                    }
                }
                if (newState == 0) {
                    adapter.reload()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        adapter.reloadAll()
    }

    private fun onRefreshLayout() {
        if (!context?.let { AppUtil.checkInternet(it) }!!) {
            binding.swRefeshLayout.isRefreshing = false
            return
        }
        binding.swRefeshLayout.isRefreshing = true
        (activity as MainActivity).refreshApi()
    }

    override fun netWorkStateChanged(isNetWork: Boolean) {
        if (!isNetWork && HawkData.getListThemes().size < 10) {
            binding.llNoNetwork.visibility = View.VISIBLE
        } else {
            binding.llNoNetwork.visibility = View.GONE
            if (HawkData.getListThemes().size < 10) {
                binding.llLoading.visibility = View.VISIBLE
                (activity as MainActivity).refreshApi()
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLoadVideo(eventBusMain: EventBusMain) {
        if (eventBusMain.isRefresh) {
            init()
        } else {
            binding.swRefeshLayout.isRefreshing = false
            listThemes = HawkData.getListThemes()
            adapter.resetListTheme()
            binding.llLoading.visibility = View.GONE
            if (listThemes.size > 5) {
                adapter.notifyItemRangeChanged(4, listThemes.size - 4)
            }
        }
        EventBus.getDefault().removeStickyEvent(eventBusMain)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        if (networkChangeReceiver != null) {
            networkChangeReceiver!!.unregisterReceiver(context)
        }
    }

    override fun onItemClick(
            themes: MutableList<Theme>,
            position: Int,
            isDelete: Boolean,
            posRandom: Int
    ) {
        moveApplyTheme(themes, position, isDelete, posRandom)
    }

    private fun moveApplyTheme(
            themes: MutableList<Theme>,
            position: Int,
            isDelete: Boolean,
            posRandom: Int,
    ) {
        val theme: Theme = themes[position]
        Log.e("TAN", "onItemClick: " + position+"--"+theme)

        if (!theme.path_file.contains("/data/data")) {
            posDownload = position
        }
        val intent = Intent(activity, ApplyActivity::class.java)
        intent.putExtra(Constant.FR_SCREEN, Constant.THEME_FRAG_MENT)
        intent.putExtra(Constant.ITEM_POSITION, position)
        intent.putExtra(Constant.POS_RANDOM, posRandom)
        if (isDelete) {
            intent.putExtra(IS_DELETE, true)
        }
        val gson = Gson()
        intent.putExtra(Constant.THEME, gson.toJson(theme))
        requireActivity().startActivity(intent)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEBApplyTheme(ebApplyTheme: EBApplyTheme) {
        when (ebApplyTheme.action) {
            Constant.INTENT_DOWNLOAD_COMPLETE_THEME -> if (posDownload != -1) {
                adapter.resetListTheme()
                adapter.notifyItemChanged(posDownload)
            }
            Constant.INTENT_APPLY_THEME -> adapter.notifyDataSetChanged()
            Constant.APPLY_THEME_DEFAULT -> adapter.notifyItemChanged(0)
        }
        EventBus.getDefault().removeStickyEvent(ebApplyTheme)
    }
}