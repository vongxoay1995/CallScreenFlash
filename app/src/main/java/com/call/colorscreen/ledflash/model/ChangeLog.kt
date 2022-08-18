package com.call.colorscreen.ledflash.model

import com.google.gson.annotations.SerializedName

class ChangeLog {
    @SerializedName("version")
    var version = 0
    @SerializedName("description")
    var description: String? = null
}