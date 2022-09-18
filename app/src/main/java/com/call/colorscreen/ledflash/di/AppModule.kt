package com.call.colorscreen.ledflash.di

import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.database.RoomDatabaseHelper
import com.call.colorscreen.ledflash.ui.contact.SelectContactModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { RoomDatabaseHelper.get(androidApplication()) }
    single { AppDatabase.getInstance(androidApplication()) }
    viewModel { SelectContactModel(get())}

}