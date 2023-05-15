package com.call.colorscreen.ledflash.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesUtils {
    private var sharedPreferences: SharedPreferences? = null
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    private fun editor(): SharedPreferences.Editor? {
        return sharedPreferences!!.edit()
    }

    fun putString(key: String?, value: String?) {
        editor()!!.putString(key, value).apply()
    }

    fun putBoolean(key: String?, value: Boolean) {
        editor()!!.putBoolean(key, value).apply()
    }
    fun getString(key: String?, default: String): String {
        return sharedPreferences!!.getString(key, default)!!
    }
    fun getBoolean(key: String?, default: Boolean): Boolean {
        return sharedPreferences!!.getBoolean(key, default)
    }

    fun putInt(key: String?, value: Int) {
        editor()!!.putInt(key, value).apply()
    }

    fun getInt(key: String?, default: Int): Int {
        return sharedPreferences!!.getInt(key, default)
    }
}