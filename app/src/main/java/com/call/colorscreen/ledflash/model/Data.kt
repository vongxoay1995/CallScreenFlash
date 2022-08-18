package com.call.colorscreen.ledflash.model

import com.call.colorscreen.ledflash.database.Theme
import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("data")
    lateinit var app: MutableList<Theme>
    @SerializedName("change-log")
    var changeLog: ChangeLog? = null
}