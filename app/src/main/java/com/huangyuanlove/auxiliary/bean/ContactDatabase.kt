package com.huangyuanlove.auxiliary.bean

import android.content.Context
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

    companion object{
        private var INSTANCE:ContactDatabase? = null
        fun init(context: Context){
            if(INSTANCE == null){
                synchronized(this){
                    if(INSTANCE == null){
                        INSTANCE = Room.databaseBuilder(context,ContactDatabase::class.java,"contact_db").build()
                    }
                }
            }

        }
        fun getInstance():ContactDatabase{
            return INSTANCE ?: throw NullPointerException("ContactDatabase未初始化")
        }

    }

}

