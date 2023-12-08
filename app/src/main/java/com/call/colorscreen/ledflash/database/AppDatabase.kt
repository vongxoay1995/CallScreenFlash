package com.call.colorscreen.ledflash.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Theme::class,Contact::class], version = 1, exportSchema = false)
abstract class AppDatabase  : RoomDatabase() {
    abstract fun serverDao(): RoomDao

    companion object {
        private const val DB_NAME = "call_screen"
        fun getInstance(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .allowMainThreadQueries()
                .addMigrations(object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                    }
                })
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {

                        super.onOpen(db)
                    }
                })
                .build()
        }
    }
}