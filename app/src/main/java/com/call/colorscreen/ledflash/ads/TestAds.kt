package com.call.colorscreen.ledflash.ads

import android.content.Context
import android.provider.Settings
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.MobileAds
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class TestAds {
    var googleDeviceId = ""
        private set

    fun generateDeviceId(context: Context) {
        val androidId = Settings.Secure.getString(context.contentResolver, "android_id")
        googleDeviceId = MD5(androidId).toUpperCase(Locale.getDefault())
        val testDeviceIds = Arrays.asList(googleDeviceId)
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
    }

    private fun MD5(md5: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val array = md.digest(md5.toByteArray())
            val builder = StringBuilder()
            val var6 = array.size
            for (var7 in 0 until var6) {
                val b = array[var7]
                builder.append(Integer.toHexString(b.toInt() and 255 or 256).substring(1, 3))
            }
            builder.toString()
        } catch (var9: NoSuchAlgorithmException) {
            ""
        }
    }

    companion object {
        @JvmStatic
        val instance = TestAds()
    }
}