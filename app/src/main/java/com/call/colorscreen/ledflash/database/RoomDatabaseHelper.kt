package com.call.colorscreen.ledflash.database

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Theme::class, Contact::class], version = 1)
abstract class RoomDatabaseHelper : RoomDatabase() {

    companion object {

        @JvmStatic
        lateinit var roomDatabaseHelper: RoomDatabaseHelper

        @JvmStatic
        val rooCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                Log.e("TAN", "onCreate: "+db.path )
            }
        }

        @JvmStatic
        @Synchronized
        fun get(application: Application): RoomDatabaseHelper {
            if (!this::roomDatabaseHelper.isInitialized) {
                roomDatabaseHelper = Room.databaseBuilder(
                    application,
                    RoomDatabaseHelper::class.java,
                    "colorcall.db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(rooCallback)
                    .build()
            }
            return roomDatabaseHelper
        }
    }
    abstract fun roomDatabaseDao(): RoomDao

}