package com.call.colorscreen.ledflash.database

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*

class RoomManager(application: Application) {
    companion object {

        @JvmStatic
        var room: RoomManager? = null

        @JvmStatic
        fun create(application: Application) {
            if (room == null) {
                room = RoomManager(application)
            }
        }

        @JvmStatic
        fun get(): RoomManager {
            return room!!
        }
    }
    //endregion

    //region create
    private var roomDao: RoomDao

    init {
        val roomDatabase = RoomDatabaseHelper.get(application)
        this.roomDao = roomDatabase.roomDatabaseDao()
    }
    //endregion

    fun liveContactList(path:String): LiveData<MutableList<Contact>> {
        return roomDao.getContactWithPath(path)
    }
    fun liveContactListSetTheme(value:String): LiveData<MutableList<Contact>> {
        return roomDao.getContactWithPath(value)
    }
    fun liveContactListWithId(id:String): LiveData<MutableList<Contact>> {
        return roomDao.getContactWithId(id)
    }
    fun liveContactListDeleteWithId(id:String){
        roomDao.deleteContactWithId(id)
    }










}