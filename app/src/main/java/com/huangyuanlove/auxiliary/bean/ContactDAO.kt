package com.huangyuanlove.auxiliary.bean

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContactDAO {

    @Query("SELECT * FROM contact")
     fun getAll():List<Contact>

    @Insert
     fun insertContact(contact :Contact)
    @Delete
     fun deleteContact(contact:Contact)

}