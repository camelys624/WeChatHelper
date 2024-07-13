package com.huangyuanlove.auxiliary.utils

import android.content.Context
import com.huangyuanlove.auxiliary.bean.MyObjectBox
import io.objectbox.BoxStore

object ObjectBox {
    lateinit var sotre:BoxStore
        private set
    fun init(context:Context){
        sotre = MyObjectBox.builder().androidContext(context).build()
    }
}