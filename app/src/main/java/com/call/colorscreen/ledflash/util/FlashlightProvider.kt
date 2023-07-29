package com.call.colorscreen.ledflash.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Camera.Parameters.FLASH_MODE_OFF
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.call.colorscreen.ledflash.R

class FlashlightProvider(val context: Context?) {
    private var mCamera: Camera? = null
    private lateinit var parameters: Camera.Parameters
    private var cameraManager = context?.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
    private var cameraId: String = ""
    private var onEverySecond: Runnable? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance : FlashlightProvider? = null

        fun getInstance(context: Context): FlashlightProvider {
            if (instance == null)  // NOT thread safe!
                instance = FlashlightProvider(context)
            return instance as FlashlightProvider
        }
    }

    init {
        if (cameraManager.cameraIdList.isNotEmpty()) {
            cameraId = cameraManager.cameraIdList[0]
        }
    }

    lateinit var thread: Thread
    var isPause = false
    var isFlashOn = false
    var isInterruptThread = false
    private var parameters1: Camera.Parameters? = null

    private fun turnOnFlash() {
        val isFlashAvailableOnDevice =
            context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        if (!isFlashAvailableOnDevice!!) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context,
                    context?.getString(R.string.flash_not_support),
                    Toast.LENGTH_SHORT
                ).show()
                if (!thread.isInterrupted){
                    thread.interrupt()
                }
            }
        } else {
            try {
                /*val cameraManager =
                    context?.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /*val cameraId = cameraManager.cameraIdList[0]*/
                    if (!isFlashOn && cameraId.isNotBlank()){
                        cameraManager.setTorchMode(cameraId, true)
                        isFlashOn = true
                    }
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    releaseCamera()
                    val open = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
                    mCamera = open
                    val parameters2 = open.parameters
                    this.parameters1 = parameters2
                    parameters2.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    mCamera!!.parameters = this.parameters1
                    if (!isInterruptThread)
                    mCamera?.startPreview()
                }
            } catch (e: Exception) {
                Log.e("TAN", "turnOnFlash: " + e.message)
            }
        }
    }
    fun turnOffFlash() {
        val isFlashAvailableOnDevice =
            context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        if (!isFlashAvailableOnDevice!!) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, R.string.flash_not_support, Toast.LENGTH_SHORT)
                    .show()
                if (thread != null && !thread.isInterrupted){
                    thread.interrupt()
                }
            }
        } else {
            try {
                /*val cameraManager =
                    context?.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    /*val cameraId = cameraManager.cameraIdList[0]*/
                    if (isFlashOn && cameraId.isNotBlank()){
                        try {
                            cameraManager.setTorchMode(cameraId, false)
                        } catch (e: Exception) {

                        } finally {
                            isFlashOn = false
                        }
                    }
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    if (mCamera == null) {
                        val open = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
                        mCamera = open
                        val parameters2 = open.parameters
                        parameters = parameters2
                        parameters2.flashMode = FLASH_MODE_OFF
                        mCamera!!.parameters = parameters
                    }
                    mCamera!!.stopPreview()
                    //them vao fix
                    // releaseCamera()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun releaseCamera() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && mCamera != null) {
            try {
                mCamera!!.stopPreview()
                mCamera!!.setPreviewCallback(null)
                mCamera!!.release()
                mCamera = null
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    fun flashCall() {
        var flashOn = false
        onEverySecond = Runnable {
            try {
                if (isPause) {
                    turnOffFlash()
                    isInterruptThread=true
                    thread.interrupt()
                }else{
                    while (!isPause&&!Thread.currentThread().isInterrupted){
                        flashOn = !flashOn
                        if (flashOn) {
                            turnOnFlash()
                            Thread.sleep(350)
                        } else {
                            turnOffFlash()
                            Thread.sleep(350)
                        }
                    }
                }
            }catch (e:Exception){
                Log.e("TAN", "flashCall: exxxx"+e.message )
            }
        }
        thread = Thread(onEverySecond)
        thread.start()
    }

}