package com.call.colorscreen.ledflash.ui.main.custom

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
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
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.util.Constant

class CustomThemeAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listBg: ArrayList<Theme>? = null
    fun setNewListBg() {
        listBg = ArrayList<Theme>()
        listBg!!.add(Theme(0, "", "", false))
        listBg!!.addAll(DataManager.query().getBackgroundDao().queryBuilder().list())
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.img_item_thumb_theme)
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

        @BindView(R.id.vdo_background_call)
        var vdo_background_call: TextureViewHandleClick? = null

        @BindView(R.id.btnAccept)
        var btnAccept: ImageView? = null
        private var backgroundSelected: Background? = null
        private var position = 0
        private var posRandom = 0
        fun onBind(i: Int) {
            position = i
            initInfor()
            backgroundSelected = HawkHelper.getBackgroundSelect()
            val background: Theme = listBg!![i]
            val pathFile: String
            if (!background.getPathThumb().equals("")) {
                pathFile = background.getPathThumb()
                Glide.with(context.applicationContext)
                    .load(pathFile)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .thumbnail(0.1f)
                    .into(imgThumb!!)
            }
            if (background.getPathThumb()
                    .equals(backgroundSelected.getPathThumb()) && HawkHelper.isEnableColorCall()
            ) {
                layoutSelected!!.visibility = View.VISIBLE
                layoutBorderItemSelect!!.visibility = View.VISIBLE
                if (!checkIsImage(background.getPathItem())) {
                    vdo_background_call.setVisibility(View.VISIBLE)
                    imgThumb!!.visibility = View.GONE
                    processVideo(background)
                } else {
                    imgThumb!!.visibility = View.VISIBLE
                }
                startAnimation()
                if (listener != null) {
                    listener!!.onItemThemeSelected(position)
                }
            } else {
                if (!checkIsImage(background.getPathItem())) {
                    vdo_background_call.stopPlayback()
                }
                vdo_background_call.setVisibility(View.GONE)
                imgThumb!!.visibility = View.VISIBLE
                layoutSelected!!.visibility = View.GONE
                layoutBorderItemSelect!!.visibility = View.GONE
                btnAccept!!.clearAnimation()
            }
        }

        private fun initInfor() {
            posRandom = position % 10
            val pathAvatar: String = Constant.avatarRandom.get(posRandom)
            val name: String = Constant.nameRandom.get(posRandom)
            val phone: String = Constant.phoneRandom.get(posRandom)
            Glide.with(context.applicationContext)
                .load("file:///android_asset/avatar/$pathAvatar")
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.1f)
                .into(imgAvatar!!)
            txtName!!.text = name
            txtPhone!!.text = phone
        }

        fun checkIsImage(path: String): Boolean {
            return if (path.contains(".jpg")
                || path.contains(".webp")
                || path.contains(".jpeg")
                || path.contains(".gif")
                || path.contains(".tiff")
                || path.contains(".png")
            ) {
                true
            } else false
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun listener() {
            imgThumb!!.setOnClickListener { v: View? ->
                if (listener != null) {
                    listener!!.onItemClick(
                        listBg,
                        position,
                        listBg!![position].getDelete(),
                        posRandom
                    )
                }
            }
            vdo_background_call.setOnClickListener { v ->
                if (listener != null) {
                    listener!!.onItemClick(
                        listBg,
                        position,
                        listBg!![position].getDelete(),
                        posRandom
                    )
                }
            }
        }

        fun startAnimation() {
            val anim8 = AnimationUtils.loadAnimation(context, R.anim.anm_accept_call)
            btnAccept!!.startAnimation(anim8)
        }

        private fun processVideo(background: Theme) {
            val sPath: String
            val sPathThumb: String
            val uriPath = "android.resource://" + context.packageName + background.getPathItem()
            if (!background.getPathThumb().equals("")) {
                sPathThumb = background.getPathThumb()
                Glide.with(context.applicationContext)
                    .load(sPathThumb)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .thumbnail(0.1f)
                    .into(imgThumb!!)
            }
            if (background.getPathItem().contains("storage") || background.getPathItem()
                    .contains("/data/data") || background.getPathItem().contains("data/user/")
            ) {
                sPath = background.getPathItem()
                if (!sPath.startsWith("http")) {
                    vdo_background_call.setVideoURI(Uri.parse(sPath))
                    playVideo()
                }
            } else {
                vdo_background_call.setVideoURI(Uri.parse(uriPath))
                playVideo()
            }
        }

        private fun playVideo() {
            vdo_background_call.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.setLooping(true)
                mediaPlayer.setVolume(0.0f, 0.0f)
            }
            vdo_background_call.setOnErrorListener { mp, what, extra ->
                vdo_background_call.stopPlayback()
                vdo_background_call.setVisibility(View.GONE)
                imgThumb!!.visibility = View.VISIBLE
                false
            }
            vdo_background_call.setOnInfoListener { mp, what, extra ->
                if (what === MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    Handler().postDelayed({
                        vdo_background_call.setAlpha(1.0f)
                        imgThumb!!.visibility = View.INVISIBLE
                    }, 100)
                    return@setOnInfoListener true
                }
                false
            }
            vdo_background_call.start()
        }

        init {
            ButterKnife.bind(this, itemView)
            resizeItem(context, layout_item)
            listener()
        }
    }

    var listener: Listener? = null

    inner class AddHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.layoutAdd)
        var layoutAdd: RelativeLayout? = null

        init {
            ButterKnife.bind(this, itemView)
            resizeItemAdd(context, layoutAdd)
            layoutAdd!!.setOnClickListener { v: View? ->
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
            backgrounds: ArrayList<Background>?,
            position: Int,
            delete: Boolean,
            posRandom: Int
        )
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return when (i) {
            0 -> AddHolder(
                LayoutInflater.from(viewGroup.context).inflate(R.layout.add_new, viewGroup, false)
            )
            1 -> ViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_theme, viewGroup, false)
            )
            else -> null
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

    fun setListener(listener2: Listener?) {
        listener = listener2
    }

    fun reload() {
        notifyItemRangeChanged(0, itemCount, 4)
    }

    init {
        setNewListBg()
    }
}
