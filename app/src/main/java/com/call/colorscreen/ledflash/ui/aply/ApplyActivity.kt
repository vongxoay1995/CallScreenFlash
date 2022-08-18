package com.call.colorscreen.ledflash.ui.aply

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.base.BaseActivity
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.ActivityApplyBinding
import com.call.colorscreen.ledflash.model.EBApplyCustom
import com.call.colorscreen.ledflash.model.EBApplyTheme
import com.call.colorscreen.ledflash.ui.contact.SelectContactActivity
import com.call.colorscreen.ledflash.util.*
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus

class ApplyActivity : BaseActivity<ActivityApplyBinding>(), View.OnClickListener,
    DownloadTask.Listener, PermissionCallListener, PermissionCallContact {
    private var posRandom = 0
    private var positionTheme = 0
    private var frScreen = 0
    private var isDownload = false
    private lateinit var theme: Theme
    private lateinit var dialogDownload: Dialog
    lateinit var txtPercent: TextView
    override fun getLayoutId(): Int {
        return R.layout.activity_apply
    }

    override fun onViewReady(savedInstance: Bundle?) {
        AppUtil.overHeaderApply(this, binding.layoutHeader)
        listener()
        posRandom = intent.getIntExtra(Constant.POS_RANDOM, 0)
        positionTheme = intent.getIntExtra(Constant.ITEM_POSITION, -1)
        frScreen = intent.getIntExtra(Constant.FR_SCREEN, -1)
        initData()
    }

    private fun initData() {
        if (intent.getBooleanExtra(Constant.IS_DELETE, false)) {
            binding.imgDelete.visibility = View.VISIBLE
        } else {
            binding.imgDelete.visibility = View.GONE
        }
        val gson = Gson()
        theme =
            gson.fromJson(intent.getStringExtra(Constant.THEME), Theme::class.java)
        val themeSelect: Theme = HawkData.getThemeSelect()
        if (theme.path_file == themeSelect.path_file && HawkData.getEnableCall()
        ) {
            binding.llApply.isEnabled = false
            binding.llApply.background = resources.getDrawable(R.drawable.bg_gray_apply)
            binding.txtApply.text = getString(R.string.applied)
            binding.txtApply.setTextColor(Color.BLACK)
        } else {
            binding.llApply.isEnabled = true
            binding.llApply.background = resources.getDrawable(R.drawable.bg_blue_radius_apply)
            binding.txtApply.setTextColor(Color.WHITE)
        }
        val mPath: String
        if (theme.type == Constant.TYPE_VIDEO) {
            playVideo()
        } else {
            binding.llContact.visibility = View.VISIBLE
            binding.imgBgCall.visibility = View.VISIBLE
            mPath = if (theme.path_file.contains("default") && theme.path_file
                    .contains("thumbDefault")
            ) {
                "file:///android_asset/" + theme.path_file
            } else {
                theme.path_file
            }
            Glide.with(applicationContext)
                .load(mPath)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .thumbnail(0.1f)
                .into(binding.imgBgCall)
            binding.videoTheme.visibility = View.GONE
        }
    }

    private fun playVideo() {
        val mPathVideo: String
        val uriPath = "android.resource://" + packageName + theme.path_file
        val mPathThumb: String = if (theme.path_file.contains("default") && theme.path_file
                .contains("thumbDefault")
        ) {
            "file:///android_asset/" + theme.path_thumb
        } else {
            theme.path_thumb
        }
        Glide.with(applicationContext)
            .load(mPathThumb)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .thumbnail(0.1f)
            .into(binding.imgBgCall)
        if (theme.path_file.contains("storage") || theme.path_file
                .contains("/data/data") || theme.path_file.contains("data/user/")
        ) {
            mPathVideo = theme.path_file
            if (!mPathVideo.startsWith("http")) {
                isDownload = false
                binding.videoTheme.setVideoURI(Uri.parse(mPathVideo))
                listenerVideo()
                binding.llFooter.setPadding(
                    resources.getDimensionPixelSize(R.dimen._15sdp),
                    binding.llFooter.paddingTop,
                    resources.getDimensionPixelSize(R.dimen._15sdp),
                    binding.llFooter.paddingBottom
                )
                binding.llContact.visibility = View.VISIBLE
                binding.txtApply.text = getString(R.string.applyForAllContact)
            } else {
                isDownload = true
                binding.llContact.visibility = View.GONE
                binding.llFooter.setPadding(
                    resources.getDimensionPixelSize(R.dimen._45sdp),
                    binding.llFooter.paddingTop,
                    resources.getDimensionPixelSize(R.dimen._45sdp),
                    binding.llFooter.paddingBottom
                )
                binding.txtApply.text = getString(R.string.download)
            }
        } else {
            binding.llFooter.setPadding(
                resources.getDimensionPixelSize(R.dimen._15sdp),
                binding.llFooter.paddingTop,
                resources.getDimensionPixelSize(R.dimen._15sdp),
                binding.llFooter.paddingBottom
            )
            binding.llContact.visibility = View.VISIBLE
            binding.videoTheme.setVideoURI(Uri.parse(uriPath))
            listenerVideo()
        }
    }

    private fun listenerVideo() {
        binding.imgBgCall.visibility = View.GONE
        binding.videoTheme.visibility = View.VISIBLE
        binding.videoTheme.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(0.0f, 0.0f)
        }
        binding.videoTheme.setOnErrorListener { _, _, _ ->
            false
        }
        binding.videoTheme.start()
    }

    private fun listener() {
        binding.imgBack.setOnClickListener(this)
        binding.llContact.setOnClickListener(this)
        binding.imgDelete.setOnClickListener(this)
        binding.llApply.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imgBack -> {
                finish()
            }
            R.id.llContact -> {
                requestPermissionContact()
            }
            R.id.imgDelete -> {
                finish()
            }
            R.id.llApply -> {
                if (!AppUtil.preventClick()) return
                Log.e("TAN", "onClick: " + isDownload)
                if (isDownload) {
                    downloadTheme(theme.path_file, theme.name)
                } else {
                    PermissionUtil.checkPermissionCall(this, this)
                }
            }
        }
    }

    private fun downloadTheme(pathFile: String, name: String) {
        dialogDownload = Dialog(this)
        dialogDownload.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogDownload.setContentView(R.layout.dialog_download)
        dialogDownload.setCancelable(false)
        dialogDownload.setCanceledOnTouchOutside(false)
        dialogDownload.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        txtPercent = dialogDownload.findViewById(R.id.txtPercent)
        var path = Environment.getDataDirectory()
            .toString() + "/data/com.call.colorscreen.ledflash/themes/"
        AppUtil.createFolder(path)
        val downloadTask = DownloadTask(this)
        downloadTask.setListener(this)
        pathDownload = path + name
        downloadTask.execute(pathFile, pathDownload)
    }

    var pathDownload: String = ""

    override fun onResume() {
        super.onResume()
        binding.videoTheme.start()
        runAnimation()
    }

    private fun runAnimation() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.ani_bling_call)
        binding.imgAccept.startAnimation(anim)
    }


    override fun onPreExecute() {
        dialogDownload.show()
    }

    override fun onProgressUpdate(value: Int) {
        txtPercent.text = "$value%"
    }

    override fun onPostExecute(result: String?) {
        try {
            if (dialogDownload.isShowing) {
                dialogDownload.dismiss()
            }
        } catch (e: Exception) {
        }
        if (result != null) {
            Toast.makeText(this, getString(R.string.down_err), Toast.LENGTH_LONG).show()
        } else {
            val arr: MutableList<Theme> = HawkData.getListThemes()
            theme.path_file = pathDownload
            Log.e("TAN", "onPostExecute: " + theme.position)
            arr[theme.position].path_file = pathDownload
            HawkData.setListThemes(arr)
            binding.videoTheme.setVideoURI(Uri.parse(pathDownload))
            binding.txtApply.text = getString(R.string.applyForAllContact)
            binding.llFooter.setPadding(
                resources.getDimensionPixelSize(R.dimen._15sdp),
                binding.llFooter.paddingTop,
                resources.getDimensionPixelSize(
                    R.dimen._15sdp
                ),
                binding.llFooter.paddingBottom
            )
            binding.llContact.visibility = View.VISIBLE
            isDownload = false
            listenerVideo()
            val ebApplyTheme = EBApplyTheme(Constant.INTENT_DOWNLOAD_COMPLETE_THEME)
            EventBus.getDefault().postSticky(ebApplyTheme)
            Toast.makeText(this, getString(R.string.downloadSs), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onHasCall() {
        applyThemeCall()
    }

    private fun applyThemeCall() {
        HawkData.setThemeSelect(theme)
        HawkData.setEnableCall(true)
        Toast.makeText(applicationContext, getString(R.string.apply_done), Toast.LENGTH_SHORT)
            .show()
        binding.llApply.isEnabled = false
        binding.llApply.background = resources.getDrawable(R.drawable.bg_gray_apply)
        binding.txtApply.text = getString(R.string.applied)
        binding.txtApply.setTextColor(Color.BLACK)
        val ebApplyTheme = EBApplyTheme(Constant.INTENT_APPLY_THEME)
        val ebApplyCustom = EBApplyCustom(Constant.INTENT_APPLY_THEME)
        val bundle = Bundle()
        bundle.putString("name", theme.name)
        bundle.putInt("position", positionTheme)
        when (frScreen) {
            Constant.THEME_FRAG_MENT -> {
                EventBus.getDefault().postSticky(ebApplyTheme)
            }
            Constant.CUSTOM_FRAG_MENT -> EventBus.getDefault().postSticky(ebApplyCustom)
        }
        finish()
    }

    private fun requestPermissionContact() {
        permReqContactLauncher.launch(PermissionUtil.permissionContact)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.PERMISSION_REQUEST_CALL_PHONE && grantResults.isNotEmpty() && AppUtil.checkPermission(
                grantResults
            )
        ) {
            if (AppUtil.canDrawOverlays(this)) {
                if (!AppUtil.checkNotificationAccessSettings(this)) {
                    AppUtil.showNotificationAccess(this)
                }
            } else {
                AppUtil.checkDrawOverlayApp(this)
            }
        }
    }

    private val permReqContactLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                onHasContact()
            } else {
                Toast.makeText(
                    this,
                    "You have disabled a contacts permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onHasContact() {
        val intent = Intent(this, SelectContactActivity::class.java)
        val gson = Gson()
        intent.putExtra(Constant.THEME, gson.toJson(theme))
        startActivity(intent)
    }
}