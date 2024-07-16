package com.huangyuanlove.wehelper.service

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.huangyuanlove.wehelper.constant.WX_PACKAGE_NAME
import java.util.LinkedList
import android.graphics.Rect
import com.huangyuanlove.wehelper.utils.Action
import com.huangyuanlove.wehelper.utils.Step_Start
import com.huangyuanlove.wehelper.utils.Step_idle
import com.huangyuanlove.wehelper.utils.Step_search_and_click_video
import com.huangyuanlove.wehelper.utils.Step_search_contact
import com.huangyuanlove.wehelper.utils.Step_start_video_chat
import com.huangyuanlove.wehelper.utils.WeChatCallStepManager




private val DISCOVER_TEXT_LIST = arrayOf("通讯录")
private val VIDEO_TEXT_LIST = arrayOf("音视频通话")

/**
 * 一键拨打微信语音、视频服务。
 */
class MakeWeChatCallService : AccessibilityService() {


    // 当窗口发生的事件是我们配置监听的事件时,会回调此方法.会被调用多次
    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        if (event.packageName == WX_PACKAGE_NAME && (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)) {
            Log.e("huangyuan", "微信活动 当前步骤--> ${WeChatCallStepManager.step}")
            when (WeChatCallStepManager.step) {
                Step_Start -> {
                    handler.removeMessages(Step_Start)
                    handler.sendEmptyMessageDelayed(Step_Start, 500)
                }


                Step_search_contact -> {

                    handler.removeMessages(Step_search_contact)
                    handler.sendEmptyMessageDelayed(Step_search_contact, 2000)
                }

                Step_search_and_click_video -> {
                    handler.removeMessages(Step_search_and_click_video)
                    handler.sendEmptyMessageDelayed(Step_search_and_click_video, 2000)
                }

                Step_start_video_chat -> {
                    handler.removeMessages(Step_start_video_chat)
                    handler.sendEmptyMessageDelayed(Step_start_video_chat, 2000)
                }

                else -> {
                    Log.e("huangyuan", "当前步骤未进行 $WeChatCallStepManager.step")
                }
            }
        }
    }


    // 获取 APPLICATION 的 Window 根节点
    private fun getRootNodeInfo(): AccessibilityNodeInfo {
        return rootInActiveWindow
    }

    override fun onInterrupt() {
        //当服务要被中断时调用.会被调用多次
    }


    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Log.e("huangyuan", "handler 开始处理 ${msg.what}")
            when (msg.what) {

                Step_Start -> {
                    val result = getRootNodeInfo().clickNodeByText(DISCOVER_TEXT_LIST, 2)
                    if(result){
                        WeChatCallStepManager.step = Step_search_contact
                    }else{
                        //todo 查找返回或者取消按钮，回到首页
                    }
                }

                Step_search_contact -> {
                    var contactNode = getRootNodeInfo().getNodeByText(arrayOf(WeChatCallStepManager.name))
                    if (contactNode == null) {
                        val contactListNode = getContactListView()
                        if (contactListNode == null) {
                            Log.e("huangyuan", "没有找到联系人列表")
                        } else {
                            Log.e("huangyuan", "找到了联系人列表,开始滑动")

                            val scrollResult =
                                contactListNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                            if (scrollResult) {
                                Log.e("huangyuan", "滑动成功")
                            }
                        }


                    } else {
                        repeat(6) {
                            contactNode = contactNode?.parent
                        }
                        Log.e("huangyuan", "Click--> ${contactNode?.className}")
                        contactNode?.let {
                            val result = it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            if(result){
                                WeChatCallStepManager.step = Step_search_and_click_video
                            }else{
                                Log.e("huangyuan","点击失败-->")
                                WeChatCallStepManager.reset()
                            }
                            return
                        }

                        WeChatCallStepManager.reset()
                    }


                }

                Step_search_and_click_video -> {
                    var contactNode = getRootNodeInfo().getNodeByText(VIDEO_TEXT_LIST)
                    if (contactNode == null) {
                        Log.e("huangyuan", "没找到音视频通话按钮")
                    } else {
                        Log.e("huangyuan", "找到音视频通话按钮")
                        repeat(2) {
                            contactNode = contactNode?.parent
                        }
                        Log.e("huangyuan", "找到音视频通话按钮--> ${contactNode?.className}")
                        contactNode?.let {
                            val result = it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            if(result){
                                WeChatCallStepManager.step = Step_start_video_chat;
                            }else{
                                WeChatCallStepManager.reset()
                            }
                            return
                        }
                        WeChatCallStepManager.reset()

                    }
                }

                Step_start_video_chat -> {
                    if(WeChatCallStepManager.action == Action.video_call){
                        getRootNodeInfo().clickNodeByText(arrayOf("视频通话"), 3)
                    }else{
                        getRootNodeInfo().clickNodeByText(arrayOf("语音通话"), 3)
                    }

                    WeChatCallStepManager.reset()

                }

                else -> {
                    Log.e("huangyuan", "${msg.what} 正在开发")
                }
            }
        }
    }


    private fun getContactListView(): AccessibilityNodeInfo? {

        Log.e("huangyuan", "----开始遍历----")
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(rootInActiveWindow)
        var info: AccessibilityNodeInfo?
        while (!queue.isEmpty()) {
            info = queue.poll()
            if (info == null) {
                continue
            }

            info.log()
            if (info.className.equals("androidx.recyclerview.widget.RecyclerView") && info.isScrollable) {
                return info
            }

            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }


        }
        Log.e("huangyuan", "----结束遍历----")
        return null
    }


    private fun AccessibilityNodeInfo.log() {
        val nodeInfo = StringBuilder("nodeInfo--> ${className} ,")
        val rect = Rect()
        getBoundsInScreen(rect)
        nodeInfo.append("rect:$rect ,width:${rect.width()} ,height:${rect.height()} ,clickable:${isClickable} ,isScrollable:${isScrollable}")
        Log.e("huangyuan", nodeInfo.toString())
    }


    /**
     * 点击指定文案的节点
     * @param textList 选文案列表，按顺序查找，找到即停止查找
     * @param parentCount 0 表示点击查找到的节点，大于 0 表示点击向上查找指定层级的父节点
     */
    private fun AccessibilityNodeInfo.clickNodeByText(
        textList: Array<String>,
        parentCount: Int = 0
    ): Boolean {
        var node = getNodeByText(textList)
        if (node != null) {
            Log.e("huangyuan", "找到了 ${textList[0]}")
        } else {
            Log.e("huangyuan", "没有找到 ${textList[0]}")
        }
        repeat(parentCount) {
            node = node?.parent
        }
        Log.e("huangyuan", "Click--> ${node?.className}")
        node?.let {
            return it.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        return false
    }

    /**
     * 查找指定文案的节点
     * @param textList 备选文案列表，按顺序查找，找到即停止查找
     * @return 对应的节点或者 null
     */
    private fun AccessibilityNodeInfo.getNodeByText(textList: Array<String>): AccessibilityNodeInfo? {
        var node: AccessibilityNodeInfo? = null
        var index = 0
        while (index < textList.size && node === null) {
            node = this.findAccessibilityNodeInfosByText(textList[index]).getOrNull(0)
            index++
        }
        return node
    }


    private fun visitNodeInfo(nodeInfo: AccessibilityNodeInfo) {
        Log.e("huangyuan", "----开始遍历----")
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(nodeInfo)
        var info: AccessibilityNodeInfo?
        while (!queue.isEmpty()) {
            info = queue.poll()
            if (info == null) {
                continue
            }

            info.log()


            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }


        }
        Log.e("huangyuan", "----结束遍历----")
    }

}