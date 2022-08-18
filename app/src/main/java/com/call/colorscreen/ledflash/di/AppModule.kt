package com.call.colorscreen.ledflash.di

import com.call.colorscreen.ledflash.database.RoomDatabaseHelper
import com.call.colorscreen.ledflash.ui.contact.SelectContactActivity
import com.call.colorscreen.ledflash.ui.contact.SelectContactModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single { RoomDatabaseHelper.get(androidApplication()) }
    single { SelectContactModel(androidApplication()) }
}