package com.call.colorscreen.ledflash.ui.main.custom

import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.analystic.Analystic
import com.call.colorscreen.ledflash.base.BaseFragmentt
import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.databinding.FragmentCustomBinding
import com.call.colorscreen.ledflash.model.EBApplyCustom
import com.call.colorscreen.ledflash.model.EBApplyTheme
import com.call.colorscreen.ledflash.ui.aply.ApplyActivity
import com.call.colorscreen.ledflash.ui.listener.DialogGalleryListener
import com.call.colorscreen.ledflash.ui.main.themes.SimpleDividerItemDecoration
import com.call.colorscreen.ledflash.util.AppUtil
import com.call.colorscreen.ledflash.util.Constant
import com.call.colorscreen.ledflash.util.Constant.IS_DELETE
import com.call.colorscreen.ledflash.util.FileUtil
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import java.io.File

class CustomFragment : BaseFragmentt<FragmentCustomBinding>(), CustomThemeAdapter.Listener,
    DialogGalleryListener {
    var adapter: CustomThemeAdapter? = null
    private var pathUriImage: String? = null
    val database by inject<AppDatabase>()
    private lateinit var analystic: Analystic

    private var positionItemThemeSelected = -1
    override fun init() {
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        binding.rcvBgMyTheme.layoutManager = gridLayoutManager
        binding.rcvBgMyTheme.itemAnimator = DefaultItemAnimator()
        binding.rcvBgMyTheme.addItemDecoration(SimpleDividerItemDecoration(AppUtil.dpToPx(5)))
        val animator: ItemAnimator = binding.rcvBgMyTheme.itemAnimator!!
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        adapter = context?.let { CustomThemeAdapter(it, database) }
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
        analystic = Analystic.getInstance(requireActivity())
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.CODE_VIDEO) {
                val uriData = data!!.data
                val path: String = FileUtil.getRealPathFromUri(requireContext(), uriData)!!
                Log.e("TAN", "onActivityResult:11111" + path)

                resetListVideo(path)
                adapter!!.setNewListBg()
                adapter!!.notifyDataSetChanged()
            } else if (requestCode == Constant.CODE_IMAGE) {
                Log.e("TAN", "onActivityResult: " + data + "--" + data!!.data)

                val path: String
                if (data != null && data.data != null) {
                    path = FileUtil.getRealPathFromUri(requireContext(), data.data)!!
                    Log.e("TAN", "onActivityResult:1 " + path)

                } else {
                    Log.e("TAN", "onActivityResult:2 ")
                    path = pathUriImage!!
                }
                resetListImage(path)
                adapter!!.setNewListBg()
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun resetListImage(path: String?) {
        if (path != null) {
            val folder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                        + Constant.PATH_THUMB_CALL_IMAGE
            )
            if (!folder.exists()) folder.mkdirs()
            val file = File(path)
            if (file.exists()) {
                val picture = Theme(
                    1, file.absolutePath, file.absolutePath, true,
                    file.absolutePath.substring(file.absolutePath.lastIndexOf("/") + 1)
                )
                Log.e("TAN", "resetListImage: "+picture.path_thumb)
                database.serverDao().saveTheme(picture)

            } else {
                Toast.makeText(context, getString(R.string.file_not_found), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun resetListVideo(path: String?) {
        val listThemeDb: ArrayList<Theme> = database.serverDao().getListTheme() as ArrayList<Theme>
        if (path != null) {
            val bitmap =
                ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND)
            val folder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + Constant.PATH_THUMB_CALL_VIDEO
            )
            val folderInternal = File(
                (requireActivity().filesDir.absolutePath+ Constant.PATH_THUMB_CALL_VIDEO)
            )
            if (!folder.exists()) folder.mkdirs()
            if (!folderInternal.exists()) folderInternal.mkdirs()

            val video: Theme
            var imageUrl = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageUrl =
                    ((requireActivity().filesDir.absolutePath + Constant.PATH_THUMB_CALL_VIDEO) + "thumb_" + listThemeDb.size+".jpg")
                video = Theme(
                    0,
                    imageUrl,
                    path,
                    true,
                    path.substring(path.lastIndexOf("/") + 1)
                )
                bitmap?.let {
                    FileUtil.saveBitmap(
                        requireActivity().filesDir.absolutePath
                                + Constant.PATH_THUMB_CALL_VIDEO + "thumb_" + listThemeDb.size+".jpg", it
                    )
                }
            } else {
                imageUrl =
                    (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                            + Constant.PATH_THUMB_CALL_VIDEO + "thumb_" + listThemeDb.size+".jpg")
                video = Theme(
                    0,
                    imageUrl,
                    path,
                    true,
                    path.substring(path.lastIndexOf("/") + 1)
                )
                bitmap?.let { FileUtil.saveBitmap(imageUrl, it) }
            }
            Log.e("TAN", "resetListVideo: "+video)
            database.serverDao().saveTheme(video)
        }
    }

    override fun onAdd() {
        checkPermissionActionCamera()
    }

    override fun onItemThemeSelected(position: Int) {
        positionItemThemeSelected = position
    }

    override fun onItemClick(
        backgrounds: ArrayList<Theme>?,
        position: Int,
        delete: Boolean,
        posRandom: Int
    ) {
        backgrounds?.let { moveApplyTheme(it, position, delete, posRandom) }
    }
    private fun moveApplyTheme(
        backgrounds: java.util.ArrayList<Theme>,
        position: Int,
        delete: Boolean,
        posRandom: Int
    ) {
        val background: Theme = backgrounds[position]
        val intent = Intent(activity, ApplyActivity::class.java)
        intent.putExtra(Constant.FR_SCREEN, Constant.CUSTOM_FRAG_MENT)
        if (delete) {
            intent.putExtra(IS_DELETE, true)
        }
        val gson = Gson()
        intent.putExtra(Constant.THEME, gson.toJson(background))
        intent.putExtra(Constant.POS_RANDOM, posRandom)
        requireActivity().startActivity(intent)
    }

    fun checkPermissionActionCamera() {
        Log.e("TAN", "checkPermissionActionCamera: ")
        val permistion: Array<String> = if (Build.VERSION.SDK_INT <= 28) {
            arrayOf(
                permission.READ_EXTERNAL_STORAGE,
                permission.WRITE_EXTERNAL_STORAGE,
                permission.CAMERA
            )
        } else {
            arrayOf(
                permission.READ_EXTERNAL_STORAGE,
                permission.CAMERA
            )
        }
        Log.e("TAN", "checkPermissionActionCamera: " + permistion)
        if (!AppUtil.checkPermission(context, permistion)) {
            Log.e("TAN", "checkPermissionActionCamera:2 ")
            requestPermissions(
                permistion,
                Constant.PERMISSION_REQUEST_CAMERA
            )
        } else {
            openDialogGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.PERMISSION_REQUEST_CAMERA && grantResults.isNotEmpty() && AppUtil.checkPermissionGrand(
                grantResults
            )
        ) {
            openDialogGallery()
        }
    }

    private fun openDialogGallery() {
        Log.e("TAN", "openDialogGallery: ")
        AppUtil.showDialogGallery(activity, analystic,this)
    }

    override fun onVideoClicked() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        photoPickerIntent.type = "video/*"
        photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        val takePhotoIntent = Intent("android.media.action.VIDEO_CAPTURE")
        val chooserIntent =
            Intent.createChooser(photoPickerIntent, resources.getString(R.string.your_video))
        chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", arrayOf(takePhotoIntent))
        startActivityForResult(chooserIntent, Constant.CODE_VIDEO)
    }

    override fun onImagesClicked() {
        pathUriImage = AppUtil.openCameraIntent(this, requireActivity(), Constant.CODE_IMAGE)
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEBApplyCustom(ebApplyCustom: EBApplyCustom) {
        Log.e("TAN", "onSignApplyCT: vaoooooo")
        when (ebApplyCustom.action) {
            Constant.INTENT_APPLY_THEME -> adapter!!.notifyDataSetChanged()
            Constant.DELETE_THEME -> {
                Log.e("TAN", "onSignApplyCT: 2222")
                adapter!!.setNewListBg()
                adapter!!.notifyDataSetChanged()
            }
        }
        EventBus.getDefault().removeStickyEvent(ebApplyCustom)
    }
}