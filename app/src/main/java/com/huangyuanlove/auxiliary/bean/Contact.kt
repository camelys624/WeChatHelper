package com.huangyuanlove.auxiliary.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "contact")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id:Int = 0,
    @ColumnInfo(name="name")
    var name:String = "",
    @ColumnInfo(name="phone")
    var phone:String = "",
    @ColumnInfo(name="avatar")
    var avatar:String = "",
    @Ignore
    var last:Boolean =false
    )