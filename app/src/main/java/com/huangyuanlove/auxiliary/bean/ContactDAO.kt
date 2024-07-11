package com.huangyuanlove.auxiliary.bean

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContactDAO {

    @Query("SELECT * FROM contact")
    suspend fun getAll():List<Contact>

    @Insert
    suspend fun insertContact(contact :Contact)
    @Delete
    suspend fun deleteContact(contact:Contact)

}