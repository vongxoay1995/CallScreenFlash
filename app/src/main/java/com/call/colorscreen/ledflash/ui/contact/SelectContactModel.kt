package com.call.colorscreen.ledflash.ui.contact

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.call.colorscreen.ledflash.database.AppDatabase
import com.call.colorscreen.ledflash.database.Contact
import com.call.colorscreen.ledflash.database.RoomDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectContactModel(val db:AppDatabase): ViewModel() {
    var listContactLiveData: MutableLiveData<MutableList<Contact>?> = MutableLiveData()

    fun liveContactList(path:String): LiveData<MutableList<Contact>> {
        return db.serverDao().getContactWithPath(path)
    }

    fun deleteContact(db:AppDatabase,id:String){
       viewModelScope.launch(Dispatchers.IO) {
                  db.serverDao().deleteContactWithId(id)
       }
   }
    fun getListContact(db:RoomDatabaseHelper,id:String){
        viewModelScope.launch(Dispatchers.IO) {
            val list = db.roomDatabaseDao().getContactById(id)
            Log.e("TAN", "getListContact: "+list.size )
           // Log.e("TAN", "getListContact: "+list )
            viewModelScope.launch(Dispatchers.Main){
                Log.e("TAN", "getListContact: set", )
                listContactLiveData.value = list
            }
        }
    }

    fun updateContact(db:AppDatabase,contact:Contact) {
            db.serverDao().update(contact)
    }

    fun insertDb(db: AppDatabase, contact: Contact) {
            db.serverDao().insert(contact)
    }
}