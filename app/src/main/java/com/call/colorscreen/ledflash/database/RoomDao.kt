package com.call.colorscreen.ledflash.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RoomDao {
    @Query("SELECT * FROM contact WHERE theme_path = :path")
    fun getContactWithPath(path: String): LiveData<MutableList<Contact>>

    @Query("SELECT * FROM contact WHERE theme_path = :path")
    fun getContactWithPath2(path: String): MutableList<Contact>

    @Query("SELECT * FROM theme ")
    fun getListTheme(): MutableList<Theme>

    @Query("SELECT * FROM contact WHERE contact_id = :id")
    fun getContactWithId(id: String): LiveData<MutableList<Contact>>
    @Query("DELETE FROM contact where contact_id = :id")
    fun deleteContactWithId(id:String)
    @Query("SELECT * FROM contact WHERE contact_id =:id")
    fun getContactById(id: String): MutableList<Contact>
    @Update
    fun update(contact: Contact?)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contact: Contact?)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTheme(theme: Theme)
}