package com.huangyuanlove.auxiliary

import android.app.Application
import com.hjq.toast.Toaster
import com.huangyuanlove.auxiliary.utils.ContextUtil
import com.huangyuanlove.auxiliary.utils.ObjectBox

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Toaster.init(this)
       ObjectBox.init(this)
    }
}