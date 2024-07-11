package com.huangyuanlove.auxiliary.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "contact")
data class Contact(
    @ColumnInfo(name="name") var name:String = "",
    @ColumnInfo(name="phone") var phone:String = "",
    @ColumnInfo(name="avatar") var avatar:String = "",
    ) {

    @PrimaryKey(autoGenerate = true)
    @NotNull
    @ColumnInfo(name="id")
    private var id:Int = 0
}