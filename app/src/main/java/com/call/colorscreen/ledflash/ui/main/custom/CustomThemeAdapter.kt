package com.call.colorscreen.ledflash.ui.main.custom

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.ItemCustomBinding
import com.call.colorscreen.ledflash.databinding.ItemThemeBinding
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.HawkData
import com.call.colorscreen.ledflash.view.TextureVideoView
import kotlinx.coroutines.NonDisposableHandle.parent

class CustomThemeAdapter(private val context: Context,val database: AppDatabase) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listBg: ArrayList<Theme>? = null
    fun setNewListBg() {
        listBg = ArrayList()
        listBg!!.add(Theme(0, "", "", false))
        listBg!!.addAll(database.serverDao().getListTheme())
       // listBg!!.addAll(DataManager.query().getBackgroundDao().queryBuilder().list())
    }

    private fun resizeItem(context: Context, layout_item: RelativeLayout?) {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val layoutParams = layout_item!!.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = (width.toFloat() / 2.1f).toInt()
        layoutParams.height = 5 * width / 6
        layout_item.layoutParams = layoutParams
    }

    private fun resizeItemAdd(context: Context, layout_item: RelativeLayout?) {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val layoutParams = layout_item!!.layoutParams as GridLayoutManager.LayoutParams
        layoutParams.width = (width / 2.1).toInt()
        layoutParams.height = 5 * width / 6
        layout_item.layoutParams = layoutParams
    }

    inner class ViewHolder(val binding: ItemThemeBinding) : RecyclerView.ViewHolder(binding.root)  {
        /*@BindView(R.id.img_item_thumb_theme)
        var imgThumb: ImageView? = null

        @BindView(R.id.layout_item)
        var layout_item: RelativeLayout? = null

        @BindView(R.id.imgAvatar)
        var imgAvatar: ImageView? = null

        @BindView(R.id.txtName)
        var txtName: TextView? = null

        @BindView(R.id.txtPhone)
        var txtPhone: TextView? = null

        @BindView(R.id.layoutSelected)
        var layoutSelected: ConstraintLayout? = null

        @BindView(R.id.layoutBorderItemSelect)
        var layoutBorderItemSelect: RelativeLayout? = null

        @BindView(R.id.vd_theme_call)
        var vdo_background_call: TextureVideoView? = null*/

        private var themeSelected: Theme? = null
        private var posRandom = 0
        fun onBind(i: Int) {
            initInfor()
            themeSelected = HawkData.getThemeSelect()
            val theme: Theme = listBg!![i]
            val pathFile: String
            if (theme.path_thumb != "") {
                pathFile = theme.path_thumb
                Glide.with(context.applicationContext)
                    .load(pathFile)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .thumbnail(0.1f)
                    .into(binding.imgThumb)
            }
            if (theme.path_thumb == themeSelected!!.path_thumb && HawkData.getEnableCall()) {
                binding.layoutSelected.visibility = View.VISIBLE
                binding.layoutBorderItemSelect.visibility = View.VISIBLE
                if (!checkIsImage(theme.path_file)) {
                    binding.videoThemes.visibility = View.VISIBLE
                    binding.imgThumb.visibility = View.GONE
                    processVideo(theme)
                } else {
                    binding.imgThumb.visibility = View.VISIBLE
                }
                startAnimation()
                if (listener != null) {
                    listener!!.onItemThemeSelected(position)
                }
            } else {
                if (!checkIsImage(theme.path_file)) {
                    binding.videoThemes.stopPlayback()
                }
                binding.videoThemes.visibility = View.GONE
                binding.imgThumb.visibility = View.VISIBLE
                binding.layoutSelected.visibility = View.GONE
                binding.layoutBorderItemSelect.visibility = View.GONE
                binding.btnAccept.clearAnimation()
            }
        }

        private fun initInfor() {
            posRandom = absoluteAdapterPosition % 10
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

        private fun checkIsImage(path: String): Boolean {
            return (path.contains(".jpg")
                    || path.contains(".webp")
                    || path.contains(".jpeg")
                    || path.contains(".gif")
                    || path.contains(".tiff")
                    || path.contains(".png"))
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun listener() {
            binding.imgThumb.setOnClickListener { v: View? ->
                if (listener != null) {
                    listener!!.onItemClick(
                        listBg,
                        position,
                        listBg!![absoluteAdapterPosition].delete,
                        posRandom
                    )
                }
            }
            binding.videoThemes.setOnClickListener { v ->
                if (listener != null) {
                    listener!!.onItemClick(
                        listBg,
                        position,
                        listBg!![position].delete,
                        posRandom
                    )
                }
            }
        }

        fun startAnimation() {
            val anim8 = AnimationUtils.loadAnimation(context, R.anim.ani_bling_call)
            binding.btnAccept.startAnimation(anim8)
        }

        private fun processVideo(background: Theme) {
            val sPath: String
            val sPathThumb: String
            val uriPath = "android.resource://" + context.packageName + background.path_file
            if (background.path_thumb != "") {
                sPathThumb = background.path_thumb
                Glide.with(context.applicationContext)
                    .load(sPathThumb)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .thumbnail(0.1f)
                    .into(binding.imgThumb)
            }
            if (background.path_file.contains("storage") || background.path_file
                    .contains("/data/data") || background.path_file.contains("data/user/")
            ) {
                sPath = background.path_file
                if (!sPath.startsWith("http")) {
                    binding.videoThemes.setVideoURI(Uri.parse(sPath))
                    playVideo()
                }
            } else {
                binding.videoThemes.setVideoURI(Uri.parse(uriPath))
                playVideo()
            }
        }

        private fun playVideo() {
            binding.videoThemes.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                mediaPlayer.setVolume(0.0f, 0.0f)
            }
            binding.videoThemes.setOnErrorListener { mp, what, extra ->
                binding.videoThemes.stopPlayback()
                binding.videoThemes.visibility = View.GONE
                binding.imgThumb.visibility = View.VISIBLE
                false
            }
            binding.videoThemes.setOnInfoListener { mp, what, extra ->
                if (what === MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    Handler().postDelayed({
                        binding.videoThemes.alpha = 1.0f
                        binding.imgThumb.visibility = View.INVISIBLE
                    }, 100)
                    return@setOnInfoListener true
                }
                false
            }
            binding.videoThemes.start()
        }

        init {
            ButterKnife.bind(this, itemView)
            resizeItem(context, binding.layoutItem)
            listener()
        }
    }

    var listener: Listener? = null

    inner class AddHolder(val binding: ItemCustomBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            ButterKnife.bind(this, itemView)
            resizeItemAdd(context, binding.layoutAdd)
            binding.layoutAdd.setOnClickListener { v: View? ->
                if (listener != null) {
                    listener!!.onAdd()
                }
            }
        }
    }

    interface Listener {
        fun onAdd()
        fun onItemThemeSelected(position: Int)
        fun onItemClick(
            backgrounds: ArrayList<Theme>?,
            position: Int,
            delete: Boolean,
            posRandom: Int
        )
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return when (i) {
            0 -> AddHolder(ItemCustomBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))
            1 ->ViewHolder(ItemThemeBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))
            else -> ViewHolder(ItemThemeBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            1 -> {
                (holder as ViewHolder).onBind(position)
                return
            }
            else -> return
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position != 0) {
            1
        } else 0
    }

    override fun getItemCount(): Int {
        return listBg!!.size
    }

    fun setListenerAdapter(listener2: Listener?) {
        listener = listener2
    }

    fun reload() {
        notifyItemRangeChanged(0, itemCount, 4)
    }

    init {
        setNewListBg()
    }
}
