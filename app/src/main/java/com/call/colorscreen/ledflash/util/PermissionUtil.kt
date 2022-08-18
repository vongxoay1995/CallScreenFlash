package com.call.colorscreen.ledflash.util

import android.Manifest.permission
import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class PermissionUtil {
    companion object {

        @JvmStatic val permissionFlash = arrayOf(
            permission.READ_PHONE_STATE,
            permission.CAMERA
        )
        @JvmStatic val permissionContact = arrayOf(
            permission.READ_CONTACTS
        )
        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic val permissionCall: Array<String> = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
                arrayOf(
                    permission.READ_PHONE_STATE,
                    permission.CALL_PHONE,
                    permission.READ_CONTACTS
                )
            }
            Build.VERSION.SDK_INT < Build.VERSION_CODES.P -> {
                arrayOf(
                    permission.ANSWER_PHONE_CALLS,
                    permission.READ_PHONE_STATE,
                    permission.CALL_PHONE,
                    permission.READ_CONTACTS
                )
            }
            else -> {
                arrayOf(
                    permission.ANSWER_PHONE_CALLS,
                    permission.READ_PHONE_STATE,
                    permission.CALL_PHONE,
                    permission.READ_CONTACTS
                )
            }
        }
        fun checkPermissionCall(activity: Activity?, listener: PermissionCallListener?) {
            if (!AppUtil.checkPermission(activity, permissionCall)) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    permissionCall,
                    Constant.PERMISSION_REQUEST_CALL_PHONE
                )
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!activity?.let { AppUtil.canDrawOverlays(it) }!!) {
                        Log.e("TAN", "checkPermissionCall: draw")
                        AppUtil.showDrawOverlayPermissionDialog(activity)
                    } else if (!AppUtil.checkNotificationAccessSettings(activity)) {
                        Log.e("TAN", "checkPermissionCall: notifi")
                        AppUtil.showNotificationAccess(activity)
                    } else {
                        listener?.onHasCall()
                    }
                } else {
                    listener?.onHasCall()
                }
            }
        }
        fun checkPermissionFlash(activity: Activity?, listener: PermissionFlashListener?) {
            if (!AppUtil.checkPermission(activity, permissionFlash)) {
                ActivityCompat.requestPermissions(
                    activity!!, permissionFlash,
                    Constant.PERMISSION_REQUEST_CAMERA
                )
            } else {
                listener?.onHasFlash()
            }
        }

    }
}