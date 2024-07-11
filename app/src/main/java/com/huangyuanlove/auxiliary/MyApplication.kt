package com.huangyuanlove.auxiliary

import android.app.Application
import com.huangyuanlove.auxiliary.utils.ContextUtil

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        ContextUtil.context = this
    }
}