package com.call.colorscreen.ledflash.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity(tableName = "theme")
class Theme {
    constructor()

    @PrimaryKey(autoGenerate = true)
    var id = 0

    @SerializedName("type")
    var type = 0

    @SerializedName("path-thumb")
    var path_thumb: String = ""

    @SerializedName("path-file")
    var path_file: String = ""
    @SerializedName("name")
    var name: String = ""
    @SerializedName("time_update")
    var time_update: String = ""
    var delete = false
    var position = 0

    constructor(
        id: Int,
        type: Int,
        @NotNull pathThumb: String,
        @NotNull path_file: String,
        delete: Boolean,
        name: String?,
        position: Int
    ) {
        this.id = id
        this.type = type
        this.path_thumb = pathThumb
        this.path_file = path_file
        this.delete = delete
        this.name = name!!
        this.position = position
    }
    constructor(
        id: Int,
        type: Int,
        @NotNull pathThumb: String,
        @NotNull path_file: String,
        delete: Boolean,
        name: String?,
    ) {
        this.id = id
        this.type = type
        this.path_thumb = pathThumb
        this.path_file = path_file
        this.delete = delete
        this.name = name!!
    }
    constructor(
        type: Int,
        @NotNull pathThumb: String,
        @NotNull path_file: String,
        delete: Boolean,
        name: String?,
        position: Int
    ) {
        this.type = type
        this.path_thumb = pathThumb
        this.path_file = path_file
        this.delete = delete
        this.name = name!!
        this.position = position
    }

    constructor(
        type: Int,
        @NotNull pathThumb: String,
        @NotNull path_file: String,
        delete: Boolean,
        name: String?
    ) {
        this.type = type
        this.path_thumb = pathThumb
        this.path_file = path_file
        this.delete = delete
        this.name = name!!
    }

    constructor(
        type: Int,
        @NotNull pathThumb: String,
        @NotNull path_file: String,
        delete: Boolean
    ) {
        this.type = type
        this.path_thumb = pathThumb
        this.path_file = path_file
        this.delete = delete
    }

    override fun toString(): String {
        return "Theme(id=$id, type=$type, path_thumb=$path_thumb, path_file=$path_file, name=$name, time_update=$time_update, delete=$delete, position=$position)"
    }

}