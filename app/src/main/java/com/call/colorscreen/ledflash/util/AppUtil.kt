package com.call.colorscreen.ledflash.util

import android.app.Activity
import android.app.AppOpsManager
import android.app.Dialog
import android.content.*
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.call.colorscreen.ledflash.R
import com.call.colorscreen.ledflash.analystic.Analystic
import com.call.colorscreen.ledflash.analystic.ManagerEvent
import com.call.colorscreen.ledflash.database.Theme
import com.call.colorscreen.ledflash.ui.listener.DialogDeleteCallBack
import com.call.colorscreen.ledflash.ui.listener.DialogGalleryListener
import com.call.colorscreen.ledflash.ui.main.PermissionOverActivity
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.io.File
import java.io.IOException


class AppUtil {
    companion object {
        @JvmStatic
        fun checkInternet(context: Context): Boolean {
            val cm = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            return cm!!.activeNetworkInfo != null
        }
        @JvmStatic
        fun dpToPx(dp: Int): Int {
            val density = Resources.getSystem().displayMetrics.density
            return Math.round(dp * density)
        }
        @JvmStatic
        fun overHeader(context: Context?, toolBar: View) {
            val statusBarHeight: Int
            val layoutParams = toolBar.layoutParams as ConstraintLayout.LayoutParams
            statusBarHeight =
                if (context?.let { getStatusBarHeight(it) }!! > 0) {
                    getStatusBarHeight(context)
                } else {
                    layoutParams.height / 3
                }
            layoutParams.height = layoutParams.height + statusBarHeight
            toolBar.layoutParams = layoutParams
            toolBar.setPadding(
                toolBar.paddingLeft,
                statusBarHeight,
                toolBar.paddingRight,
                toolBar.paddingBottom
            )
        }
        @JvmStatic
        fun overHeaderApply(context: Context?, toolBar: View) {
            val statusBarHeight: Int
            val layoutParams = toolBar.layoutParams as RelativeLayout.LayoutParams
            statusBarHeight =
                if (context?.let { getStatusBarHeight(it) }!! > 0) {
                    getStatusBarHeight(context)
                } else {
                    layoutParams.height / 3
                }
            layoutParams.height = layoutParams.height + statusBarHeight
            toolBar.layoutParams = layoutParams
            toolBar.setPadding(
                toolBar.paddingLeft,
                statusBarHeight,
                toolBar.paddingRight,
                toolBar.paddingBottom
            )
        }
        private fun getStatusBarHeight(context: Context): Int {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }
        fun checkPermissionGrand(grantResults: IntArray): Boolean {
            var passed = true
            for (i in grantResults) {
                if (i != 0) {
                    passed = false
                }
            }
            return passed
        }
        @JvmStatic
        fun checkPermission(grantResults: IntArray): Boolean {
            var passed = true
            for (i in grantResults) {
                if (i != 0) {
                    passed = false
                }
            }
            return passed
        }
        fun checkDrawOverlayApp(context: Context?) {
            if (Build.VERSION.SDK_INT >= 23 && !checkDrawOverlayAppNew(
                    context
                )
            ) {
                context?.let { showDrawOverlayPermissionDialog(it) }
            }
        }
       /* fun checkDrawOverlay(context: Context?): Boolean {
            return Build.VERSION.SDK_INT < 23 || context?.let { canDrawOverlays(it) } == true
        }*/

        fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
            // Set the media view.
            adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            // The headline and mediaContent are guaranteed to be in every NativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            adView.mediaView!!.setMediaContent(nativeAd.mediaContent)

            // These assets aren't guaranteed to be in every NativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adView.bodyView.visibility = View.INVISIBLE
            } else {
                adView.bodyView.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }
            if (nativeAd.callToAction == null) {
                adView.callToActionView.visibility = View.INVISIBLE
            } else {
                adView.callToActionView.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }
            if (nativeAd.icon == null) {
                adView.iconView.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon!!.drawable
                )
                (adView.iconView as ImageView).visibility = View.VISIBLE
            }
            if (nativeAd.price == null) {
                adView.priceView.visibility = View.INVISIBLE
            } else {
                adView.priceView.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }
            if (nativeAd.store == null) {
                adView.storeView.visibility = View.INVISIBLE
            } else {
                adView.storeView.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }
            if (nativeAd.starRating == null) {
                adView.starRatingView.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
                adView.starRatingView.visibility = View.VISIBLE
            }
            if (nativeAd.advertiser == null) {
                adView.advertiserView.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView.visibility = View.VISIBLE
            }
            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd)
        }

        fun checkPermission(context: Context?, permission: Array<String>): Boolean {
            for (checkSelfPermission in permission) {
                Log.e("TAN", "checkPermission: "+checkSelfPermission+"--"+ContextCompat.checkSelfPermission(context!!, checkSelfPermission) )
                if (ContextCompat.checkSelfPermission(context, checkSelfPermission) != 0) {
                    return false
                }
            }
            return true
        }
        fun showDialogGallery(activity: Activity?, analystic:Analystic,dialogGalleryListener: DialogGalleryListener?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_request_gallery)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val imgVideo: ImageView
            val imgImages: ImageView
            imgVideo = dialog.findViewById(R.id.imgSelectVideo)
            imgImages = dialog.findViewById(R.id.imgSelectImage)
            dialog.show()
            analystic.trackEvent(ManagerEvent.mainDialogOpen())
            imgVideo.setOnClickListener { v: View? ->
                if (dialogGalleryListener != null) {
                    dialogGalleryListener.onVideoClicked()
                    analystic.trackEvent(ManagerEvent.mainDialogVideo())
                }
                dialog.dismiss()
            }
            imgImages.setOnClickListener {
                if (dialogGalleryListener != null) {
                    dialogGalleryListener.onImagesClicked()
                    analystic.trackEvent(ManagerEvent.mainDialogPicture())
                }
                dialog.dismiss()
            }
        }

        fun checkDrawOverlayAppNew(context: Context?): Boolean {
            return if (context == null) {
                false
            } else canDrawOverlayViews(context)
        }

        /* public static boolean canDrawOverlays(Context context) {
     */
        /*  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return Settings.canDrawOverlays(context);
        } else {
            Log.e("TAN", "canDrawOverlays: 1");
            if (Settings.canDrawOverlays(context)) return true;
            try {
                WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (mgr == null) return false; //getSystemService might return null
                Log.e("TAN", "canDrawOverlays: 2");
                View viewToAdd = new View(context);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                viewToAdd.setLayoutParams(params);
                mgr.addView(viewToAdd, params);
                mgr.removeView(viewToAdd);
                Log.e("TAN", "canDrawOverlays: 3");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("TAN", "canDrawOverlays: 4");
            return false;
        }*/
        /*
        Log.e("TAN", "canDrawOverlays: "+checkDrawOverlayApp2(context));
        return checkDrawOverlayApp2(context);
    }*/
        fun canDrawOverlayViews(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Settings.canDrawOverlays(context)) return true
            val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                val result: Int = manager.checkOp(
                    AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                    Binder.getCallingUid(),
                    context.packageName
                )
                return result == AppOpsManager.MODE_ALLOWED
            } catch (e: java.lang.Exception) {
            }
            try {
                val mgr = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    ?: return false
                //getSystemService might return null
                val viewToAdd = View(context)
                val params = WindowManager.LayoutParams(
                    0,
                    0,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
                )
                viewToAdd.layoutParams = params
                mgr.addView(viewToAdd, params)
                mgr.removeView(viewToAdd)
                return true
            } catch (e: java.lang.Exception) {
            }
            return false
        }

       /* @RequiresApi(Build.VERSION_CODES.M)
        fun canDrawOverlays(context: Context): Boolean {
            return when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> true
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                    Settings.canDrawOverlays(context)
                }
                else -> {
                    if (Settings.canDrawOverlays(context)) return true
                    try {
                        val mgr = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                            ?: return false
                        //getSystemService might return null
                        val viewToAdd = View(context)
                        val params = WindowManager.LayoutParams(
                            0,
                            0,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSPARENT
                        )
                        viewToAdd.layoutParams = params
                        mgr.addView(viewToAdd, params)
                        mgr.removeView(viewToAdd)
                        return true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    false
                }
            }
        }*/

        fun showDrawOverlayPermissionDialog(context: Context) {
            val alertDialog = AlertDialog.Builder(context).create()
            alertDialog.setTitle(context.getString(R.string.overlay_permision))
            alertDialog.setMessage(context.getString(R.string.overlay_permision_content))
            alertDialog.setCancelable(false)
            alertDialog.setButton(
                AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.ok)
            ) { dialog: DialogInterface, _: Int ->
                var intent: Intent?
                if (Build.VERSION.SDK_INT >= 23) {
                    intent = Intent(
                        "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                        Uri.parse("package:" + context.packageName)
                    )
                    (context as Activity).startActivityForResult(intent, Constant.REQUEST_DRAW_OVER)
                    PermissionOverActivity.open(context, 0)
                }
                dialog.dismiss()
            }
            alertDialog.show()
        }

        fun checkNotificationAccessSettings(context: Context): Boolean {
            val string = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            )
            if (TextUtils.isEmpty(string)) {
                return false
            }
            for (unfastenFromString in string.split(":".toRegex()).toTypedArray()) {
                val unfastenFromString2 = ComponentName.unflattenFromString(unfastenFromString)
                if (unfastenFromString2 != null && TextUtils.equals(
                        context.packageName,
                        unfastenFromString2.packageName
                    )
                ) {
                    return true
                }
            }
            return false
        }

        fun showNotificationAccess(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(context.getString(R.string.turn_on_noti))
                .setNegativeButton(R.string.ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    (context as Activity).startActivityForResult(
                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
                        Constant.REQUEST_NOTIFICATION
                    )
                    PermissionOverActivity.open(context, 1)
                }
            builder.setCancelable(false)
            val getNotifiAcessDialog = builder.create()
            getNotifiAcessDialog.show()
        }
        fun loadDataDefault(context: Context, path: String): MutableList<Theme>? {
            val listTheme: MutableList<Theme> = mutableListOf()
            var pathThumb: String
            var pathFile: String
            var type = 0
            val prefixVideo = "/raw/"
            var theme: Theme
            try {
                val pathFiles = context.assets.list(path)
                for (i in pathFiles!!.indices) {
                    pathThumb = path + "/" + pathFiles[i]
                    if (i > 3) {
                        type = 1
                        pathFile = pathThumb
                    } else {
                        type = 0
                        pathFile = prefixVideo + pathFiles[i].substring(0, pathFiles[i].length - 5)
                    }
                    theme = Theme(type, pathThumb, pathFile, false, "default_" + (i + 1), i)
                    listTheme.add(theme)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.e("TAN", "loadDataDefault: " + listTheme)
            return listTheme
        }
        private var mLastClickTime: Long = 0
        @JvmStatic
        fun preventClick(): Boolean {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            return true
        }
        @JvmStatic
        fun createFolder(folderApp: String?) {
            val file = File(folderApp)
            if (!file.exists()) {
                file.mkdir()
            }
        }
        fun getContactName(context: Context, phoneNumber: String): ContactRetrieve {
            val cr = context.contentResolver
            val contactRetrieve: ContactRetrieve
            var contactId = ""
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            val cursor = cr.query(
                uri,
                arrayOf(
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup._ID
                ),
                null,
                null,
                null
            )
            var contactName = ""
            if (cursor != null) {
                Log.e("TAN", "getContactNameaaaaa: " + phoneNumber + "--" + cursor.count)
            }
            while (cursor!=null&&cursor.moveToNext()) {
                contactName =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            }
            /*  if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }*/Log.e("TAN", "getContactName: $contactName--$contactId")
            if (cursor!=null&&(!cursor.isClosed)) {
                cursor.close()
            }
            contactRetrieve = ContactRetrieve(contactName, contactId)
            return contactRetrieve
        }
        fun getPhotoContact(context: Context, number: String): Bitmap? {
            var photo = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.avatar
            )
            if (number != "") {
                val contentResolver = context.contentResolver
                var contactId: String? = null
                val uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number)
                )
                val projection = arrayOf(
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup._ID
                )
                val cursor = contentResolver.query(
                    uri,
                    projection,
                    null,
                    null,
                    null
                )
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        contactId =
                            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
                    }
                    cursor.close()
                }
                try {
                    if (contactId != null) {
                        val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                            context.contentResolver,
                            ContentUris.withAppendedId(
                                ContactsContract.Contacts.CONTENT_URI,
                                java.lang.Long.valueOf(contactId)
                            ), true
                        )
                        if (inputStream != null) {
                            photo = BitmapFactory.decodeStream(inputStream)
                        }
                        inputStream?.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return photo
        }
        fun openCameraIntent(fragment: Fragment, activity: Activity, requestCode: Int): String? {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val pickTitle = activity.resources.getString(R.string.select_picture)
            val chooserIntent = Intent.createChooser(photoPickerIntent, pickTitle)
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePhotoIntent))
            return if (takePhotoIntent.resolveActivity(activity.packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile(activity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        activity,
                        activity.packageName + ".provider",
                        photoFile
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        takePhotoIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            photoURI
                        )
                    }
                    fragment.startActivityForResult(chooserIntent, requestCode)
                    photoFile.absolutePath
                } else null
            } else null
        }

        @Throws(IOException::class)
        fun createImageFile(context: Context): File? {
            val storageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile("ColorCall_image_default", ".jpg", storageDir)
        }
        fun showDialogDelete(activity: Activity, dialogDeleteCallBack: DialogDeleteCallBack) {
            val inflater = activity.layoutInflater
            val alertLayout: View = inflater.inflate(R.layout.dialog_delete, null)
            val txtNo: TextView = alertLayout.findViewById(R.id.btnNo)
            val txtYes: TextView = alertLayout.findViewById(R.id.btnYes)
            val alert = AlertDialog.Builder(activity)
            alert.setView(alertLayout)
            alert.setCancelable(false)
            val dialog = alert.create()
            dialog.show()
            txtNo.setOnClickListener { dialog.dismiss() }
            txtYes.setOnClickListener {
                dialogDeleteCallBack.onDelete()
                dialog.dismiss()
            }
        }
    }
}