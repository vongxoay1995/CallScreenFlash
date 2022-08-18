package com.call.colorscreen.ledflash.model

class ContactInfor {
    lateinit var contactId: String
    var isChecked: Boolean = false
    lateinit var chosenTheme: String
    lateinit var displayName: String
    lateinit var number: String
    lateinit var photo: String

    constructor(contactId: String?, displayName: String?, number: String?, photo: String?) {
        this.contactId = contactId!!
        this.displayName = displayName!!
        this.number = number!!
        this.photo = photo!!
        this.chosenTheme = ""
    }

    constructor(
        contactId: String?,
        displayName: String?,
        number: String?,
        photo: String?,
        chosenTheme: String?
    ) {
        this.contactId = contactId!!
        this.displayName = displayName!!
        this.number = number!!
        this.photo = photo!!
        this.chosenTheme = chosenTheme!!
    }
}