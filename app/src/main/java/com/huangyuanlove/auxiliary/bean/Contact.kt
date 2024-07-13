package com.huangyuanlove.auxiliary.bean

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id


@Entity
data class Contact(
    @Id
    var id:Long = 0,
    var name:String = "",
    var phone:String = "",
    var avatar:String = "",
    var last:Boolean =false
    ){
    override fun toString(): String {
        return "Contact(id=$id, name='$name', phone='$phone', avatar='$avatar', last=$last)"
    }
}