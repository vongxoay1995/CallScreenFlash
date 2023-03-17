package com.call.colorscreen.ledflash.ui.main.themes

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.ItemThemeBinding
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.HawkData
import java.util.*

class ThemesAdapter(val context: Context) :
    RecyclerView.Adapter<ThemesAdapter.ViewHolder>() {
    private var listThemes:MutableList<Theme> = mutableListOf()
    constructor(context: Context, arr: MutableList<Theme>): this(context) {
        distributeData(arr)
    }
    open fun distributeData(data: MutableList<Theme>) {
        listThemes = data.filterIndexed { ix, element ->
             element.type == 0
        }.toMutableList()
        /*for (i in 0 until data.size) {
            Log.e("TAN", "distributeData: "+data.size+"--"+i )
            if (data[i].type == 1) {
                Log.e("TAN", "remove: "+i )
                data.removeAt(i)
                i--
            } else {
                listThemes.add(data[i])
            }
        }*/
    }
    fun resetListTheme(){
        listThemes = mutableListOf()
        distributeData(HawkData.getListThemes())
    }

    inner class ViewHolder(val binding: ItemThemeBinding) : RecyclerView.ViewHolder(binding.root) {
        private var themeSelected: Theme? = null
        private var pos = 0
        private var posRandom = 0
        init {
            resizeItem(context, binding.layoutItem)
            listener()
        }
        fun onBind(i: Int) {
            pos = i
            listener
            ranDomInfor()
            themeSelected = HawkData.getThemeSelect()
            var theme:Theme = listThemes[i]
            Log.e("TAN", "onBind theme: "+theme +"--"+themeSelected)
            var pathFile = ""
            if (theme.path_thumb != "") {
                pathFile = if (theme.path_file.contains("default")) {
                    "file:///android_asset/" + theme.path_thumb
                } else {
                    theme.path_thumb
                }
                Log.e("TAN", "pathFile: "+pathFile )
                Glide.with(context.applicationContext)
                    .load(pathFile)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .thumbnail(0.1f)
                    .into(binding.imgThumb)
            }
            if (theme.path_thumb == themeSelected!!.path_thumb && HawkData.getEnableCall()) {
                binding.layoutSelected.visibility = View.VISIBLE
                binding.layoutBorderItemSelect.visibility = View.VISIBLE
                binding.imgThumb.visibility = View.GONE
                binding.videoThemes.visibility = View.VISIBLE
                playVideo(theme)
                animation()
            } else {
                binding.videoThemes.stopPlayback()
                binding.videoThemes.visibility = View.GONE
                binding.imgThumb.visibility = View.VISIBLE
                binding.layoutSelected.visibility = View.GONE
                binding.layoutBorderItemSelect.visibility = View.GONE
                binding.btnAccept.clearAnimation()
            }
        }
        private fun listener() {
            binding.imgThumb.setOnClickListener {
                listener.onItemClick(
                    listThemes,
                    pos,
                    listThemes[pos].delete,
                    posRandom
                )
            }
            binding.videoThemes.setOnClickListener {
                listener.onItemClick(
                    listThemes,
                    pos,
                    listThemes[pos].delete,
                    posRandom
                )
            }
        }
        private fun animation() {
            val anim8 = AnimationUtils.loadAnimation(context, R.anim.ani_bling_call)
            binding.btnAccept.startAnimation(anim8)
        }

        private fun playVideo(theme: Theme) {
            val mPath: String
            val uriPath = "android.resource://" + context.packageName + theme.path_file
            if (theme.path_file!!.contains("storage") || theme.path_file!!
                    .contains("/data/data") || theme.path_file!!.contains("data/user/")
            ) {
                mPath = theme.path_file!!
                if (!mPath.startsWith("http")) {
                    binding.videoThemes.setVideoURI(Uri.parse(mPath))
                    startVideo()
                }
            } else {
                binding.videoThemes.setVideoURI(Uri.parse(uriPath))
                startVideo()
            }
        }
        private fun startVideo() {
            binding.videoThemes.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                binding.videoThemes.start()
            }
            binding.videoThemes.setOnErrorListener { _, _, _ ->
                binding.videoThemes.stopPlayback()
                binding.videoThemes.visibility = View.GONE
                binding.imgThumb.visibility = View.VISIBLE
                false
            }
            binding.videoThemes.setOnInfoListener { _, what: Int, _ ->
                if (what === MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    Handler().postDelayed({
                        binding.videoThemes.alpha = 1.0f
                        binding.imgThumb.visibility = View.INVISIBLE
                    }, 100)
                    return@setOnInfoListener true
                }
                false
            }
        }
        private fun resizeItem(context: Context, layout_item: RelativeLayout) {
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val layoutParams = layout_item.layoutParams as FrameLayout.LayoutParams
            layoutParams.width = (width.toFloat() / 2.1f).toInt()
            layoutParams.height = 5 * width / 6
            layout_item.layoutParams = layoutParams
        }

        private fun ranDomInfor() {
            posRandom = pos % 10
            val pathAvatar: String = Constant.avRandom[posRandom]
            val name: String = Constant.nameRandom[posRandom]
            val phone: String = Constant.numberRandom[posRandom]
            Glide.with(context.applicationContext)
                .load("file:///android_asset/avatar/$pathAvatar")
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.1f)
                .into(binding.imgAvatar)
            binding.txtName.text = name
            binding.txtPhone.text = phone
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return listThemes.size
    }
    fun reload() {
        notifyItemRangeChanged(0, itemCount, 4)
    }

    fun reloadAll() {
        notifyItemRangeChanged(0, itemCount, 2)
    }
    lateinit var listener: Listener
    fun setListenerClick(listener: Listener) {
        this.listener = listener
    }
    interface Listener {
        fun onItemClick(
            themes: MutableList<Theme>,
            position: Int,
            isDelete: Boolean,
            posRandom: Int
        )
    }
}