package com.huangyuanlove.wehelper.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.huangyuanlove.wehelper.service.MakeWeChatCallService


/**
 * Created by StoneHui on 2019-11-28.
 * <p>
 * 无障碍服务管理
 */
object ServiceManager {

    /**
     * 检查服务是否开启。
     */
    fun isServiceEnabled(context: Context): Boolean {
        (context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager)
                .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
                .filter { it.id == "${context.packageName}/${MakeWeChatCallService::class.java.name}" }
                .let { return it.isNotEmpty() }
    }
}

