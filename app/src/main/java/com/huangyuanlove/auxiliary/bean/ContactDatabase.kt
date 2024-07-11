package com.huangyuanlove.auxiliary.bean

import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.huangyuanlove.auxiliary.utils.ContextUtil

@Database(entities = [Contact::class], version = 1)
abstract class ContactDatabase :RoomDatabase(){
    abstract fun contactDAO():ContactDAO



}
class RoomUtils private constructor(){

    private val database:ContactDatabase by lazy{
        Room.databaseBuilder(ContextUtil.context,ContactDatabase::class.java,"contact_db").build()
    }


    companion object{

    }
}
