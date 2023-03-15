package com.call.colorscreen.ledflash.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "contact")
class Contact {
    constructor()
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var contact_id: String = ""
    var theme: String = ""
    var theme_path: String = ""
    constructor(id: Int, @NotNull contact_id: String, @NotNull theme: String,theme_path: String) {
        this.id = id
        this.contact_id = contact_id
        this.theme = theme
        this.theme_path = theme_path
    }
    constructor( @NotNull contact_id: String, @NotNull theme: String,theme_path: String) {
        this.contact_id = contact_id
        this.theme = theme
        this.theme_path = theme_path
    }

    override fun toString(): String {
        return "Contact(id=$id, contact_id='$contact_id', theme='$theme', theme_path='$theme_path')"
    }

}