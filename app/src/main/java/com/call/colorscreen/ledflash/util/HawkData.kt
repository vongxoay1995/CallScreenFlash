package com.call.colorscreen.ledflash.util

import com.call.colorscreen.ledflash.database.Theme
import com.orhanobut.hawk.Hawk

class HawkData {
    companion object {
        var KEY_FLASH :String = "KEY_FLASH"
        var VERSION_API :String = "VERSION_API"
        var THEME_SELECT :String = "THEME_SELECT"
        var KEY_ON_OFF :String = "KEY_ON_OFF"
        var IS_FIRST_LOAD_DATA :String = "IS_FIRST_LOAD_DATA"
        var TIME_LIMIT_ADS :String = "TIME_LIMIT_ADS"
        var LIST_THEME :String = "LIST_THEME"
        var LIST_THEME_DEFAULT :String = "LIST_THEME_DEFAULT"
        private const val IS_RATED = "IS_RATED"
        private const val ALLOW_RATE = "ALLOW_RATE"
        private const val COUNT_EXIT_APP = "COUNT_EXIT_APP"

        @JvmStatic
        fun setExitApp(value: Int) {
            Hawk.put(COUNT_EXIT_APP, value)
        }
        @JvmStatic
        fun getCountExitApp(): Int {
            return Hawk.get(COUNT_EXIT_APP, 0)
        }
        @JvmStatic
        fun setAllowRate(value: Boolean) {
            Hawk.put(ALLOW_RATE, value)
        }
        @JvmStatic
        fun isAllowRate(): Boolean {
            return Hawk.get(ALLOW_RATE, true)
        }
        @JvmStatic
        fun setVersion(ver: Int) {
            Hawk.put(VERSION_API, ver)
        }
        @JvmStatic
        fun getVersion(): Int {
            return Hawk.get(VERSION_API, 0)
        }
        @JvmStatic
        fun isRated(): Boolean? {
            return Hawk.get(IS_RATED, false)
        }
        @JvmStatic
        fun setRate(value: Boolean) {
            Hawk.put(IS_RATED, value)
        }
        @JvmStatic
        fun setListThemes(themes: MutableList<Theme>) {
            Hawk.put(LIST_THEME, themes)
        }
        @JvmStatic
        fun getListThemes(): MutableList<Theme> {
            return Hawk.get(LIST_THEME, mutableListOf())
        }
        @JvmStatic
        fun getThemeSelect(): Theme {
            val theme = Theme(
                0,
                0,
                "thumb/default_1.webp",
                "/raw/default_1",
                false,
                "default_1"
            )
            return Hawk.get(
                THEME_SELECT,
                theme
            )
        }
        @JvmStatic
        fun setThemeSelect(theme: Theme) {
            Hawk.put(
                THEME_SELECT,
                theme
            )
        }
        @JvmStatic
        fun setListThemesDefault(themes: MutableList<Theme>) {
            Hawk.put(LIST_THEME_DEFAULT, themes)
        }
        @JvmStatic
        fun getListThemesDefault(): MutableList<Theme> {
            return Hawk.get(LIST_THEME_DEFAULT, mutableListOf())
        }
        @JvmStatic
        fun setEnableCall(value: Boolean) {
             Hawk.put(KEY_ON_OFF, value)
        }
        @JvmStatic
        fun getEnableCall(): Boolean {
            return Hawk.get(KEY_ON_OFF, false)
        }
        @JvmStatic
        fun setEnableFlash(value: Boolean) {
            Hawk.put(KEY_FLASH, value)
        }
        @JvmStatic
        fun getEnableFlash(): Boolean {
            return Hawk.get(KEY_FLASH, false)
        }
        @JvmStatic
        fun setFirstData(value: Boolean) {
            Hawk.put(IS_FIRST_LOAD_DATA, value)
        }
        @JvmStatic
        fun isFirstData(): Boolean {
            return Hawk.get(IS_FIRST_LOAD_DATA, false)
        }
        @JvmStatic
        fun setTimeLimitInter(value: Long) {
            Hawk.put<Long>(TIME_LIMIT_ADS, value)
        }
        @JvmStatic
        fun getTimeLimitInter(): Long {
            return Hawk.get<Long>(TIME_LIMIT_ADS, 5L)
        }
    }
}