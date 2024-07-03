package com.huangyuanlove.auxiliary.service

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo


class MyAccessibilityService:AccessibilityService() {
    companion object{
        val TAG:String = "MyAccessibilityService"
        val WECHAT_PACKAGE_NAME = "com.tencent.mm"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            val eventType = event.eventType;
            log("eventType ${eventType} \t PackageName: ${event.packageName} \t source:${event.source}" )
            if(eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                if(event.packageName == WECHAT_PACKAGE_NAME){
                    val source = event.getSource()
                    source?.let {
                       val childCount = rootInActiveWindow.childCount


                    }
                }
            }

        }

    }
    fun log(msg:String){
        Log.e(TAG,msg)
    }

    private fun searchContact(contactName: String) {

    }

    override fun onInterrupt() {

    }
}