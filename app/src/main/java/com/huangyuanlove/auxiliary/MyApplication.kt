package com.huangyuanlove.auxiliary

import android.app.Application
import com.hjq.toast.Toaster
import com.huangyuanlove.auxiliary.bean.ContactDatabase
import com.huangyuanlove.auxiliary.utils.ContextUtil

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Toaster.init(this)
        ContactDatabase.init(this)
    }
}