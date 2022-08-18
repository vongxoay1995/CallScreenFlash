package com.call.colorscreen.ledflash.ui.contact

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.call.colorscreen.ledflash.database.Contact
import com.call.colorscreen.ledflash.database.RoomDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelectContactModel(application: Application): AndroidViewModel(application)  {
    var listContactLiveData: MutableLiveData<MutableList<Contact>?> = MutableLiveData()
    fun deleteContact(db:RoomDatabaseHelper,id:String){
       viewModelScope.launch(Dispatchers.IO) {
                  db.roomDatabaseDao().deleteContactWithId(id)
       }
   }
    fun getListContact(db:RoomDatabaseHelper,id:String){
        viewModelScope.launch(Dispatchers.IO) {
            val list = db.roomDatabaseDao().getContactById(id)
           // Log.e("TAN", "getListContact: "+list )
            viewModelScope.launch(Dispatchers.Main){
                listContactLiveData.value = list
            }

        }
    }

    fun updateContact(db:RoomDatabaseHelper,contact:Contact) {
        db.roomDatabaseDao().update(contact)
    }

    fun insertDb(db: RoomDatabaseHelper, contact: Contact) {
        db.roomDatabaseDao().insert(contact)
    }
}