package com.huangyuanlove.wehelper.service

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.huangyuanlove.wehelper.constant.WX_PACKAGE_NAME
import com.huangyuanlove.wehelper.utils.EasyTimer
import java.util.LinkedList
import android.graphics.Rect
import android.view.accessibility.AccessibilityWindowInfo

private const val Step_Start = 1
private const val Step_ClickSearchButton = Step_Start + 1
private const val Step_FindSearchInputText = Step_ClickSearchButton + 1
private const val Step_FindContact = Step_FindSearchInputText + 1
private const val Step_ClickContact = Step_FindContact + 1
private const val Step_FindVideoMenu = Step_ClickContact + 1
private const val Step_ClickVideoMenu = Step_FindVideoMenu + 1
private const val Step_FindVideoButton = Step_ClickVideoMenu + 1
private const val Step_Select_Video_Call = Step_FindVideoButton + 1
private const val Step_ClickVideoButton = Step_Select_Video_Call + 1


private const val Step_search_contact = 100
private const val Step_search_and_click_video = Step_search_contact + 1
private const val Step_start_video_chat = Step_search_and_click_video + 1


private const val USER_NAME = "穷的清心寡欲"


private val DISCOVER_TEXT_LIST = arrayOf("通讯录")
private val VIDEO_TEXT_LIST = arrayOf("音视频通话")
private val Contact_LIST = arrayOf(USER_NAME)

/**
 * Created by StoneHui on 2018/10/22.
 * <p>
 * 微信多图分享服务。
 */
class WXShareMultiImageService : AccessibilityService() {


    // 当前步骤
    private var currentStep = Step_Start
    private var video_parent_count = 0


    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Log.e("huangyuan", "handler 开始处理 ${msg.what}")
            when (msg.what) {
//                Step_Start -> {
//                    findSearchButtonAndClick()
//                }
//
//                Step_FindSearchInputText -> {
//                    findSearchEditTextAndInput()
//                }
//
//                Step_FindVideoMenu -> {
//                    findVideoMenuAndClick()
//
//                }
//
//                Step_FindVideoButton -> {
//                    findVideoFunctionAndClick()
//                }


                ///////////////////
                Step_Start -> {
                    getRootNodeInfo().clickNodeByText(DISCOVER_TEXT_LIST, 2)
                    currentStep = Step_search_contact
                }

                Step_search_contact -> {
//                    getRootNodeInfo().clickNodeByText(Contact_LIST, 6)
                    var contactNode = getRootNodeInfo().getNodeByText(Contact_LIST)
                    if (contactNode == null) {
                        val contactListNode = getContactListView()
                        if (contactListNode == null) {
                            Log.e("huangyuan", "没有找到联系人列表")
                        } else {
                            Log.e("huangyuan", "找到了联系人列表,开始滑动")

                            val scrollResult = contactListNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                            if (scrollResult) {
                                Log.e("huangyuan", "滑动成功")
                            }
                        }


                    }else{
                        repeat(6) {
                            contactNode = contactNode?.parent
                        }
                        Log.e("huangyuan", "Click--> ${contactNode?.className}")

                        contactNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        currentStep = Step_search_and_click_video
                    }



                }
                Step_search_and_click_video->{
                    var contactNode = getRootNodeInfo().getNodeByText(VIDEO_TEXT_LIST)
                    if(contactNode==null){
                        Log.e("huangyuan","没找到音视频通话按钮")
                    }else{
                        Log.e("huangyuan","找到音视频通话按钮")
                        repeat(2){
                            contactNode = contactNode?.parent
                        }
                        Log.e("huangyuan","找到音视频通话按钮--> ${contactNode?.className}")
                        contactNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        currentStep= Step_start_video_chat
                    }
                }
                Step_start_video_chat->{
                    getRootNodeInfo().clickNodeByText(arrayOf("视频通话"), 3)
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


    private fun findDirectoryAndClick() {
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(rootInActiveWindow)
        var info: AccessibilityNodeInfo?
        while (!queue.isEmpty()) {
            info = queue.poll()
            if (info == null) {
                continue
            }
            info.log()
            //更多功能按钮
            if (TextUtils.equals(
                    info.contentDescription,
                    "通讯录"
                ) && TextUtils.equals("android.widget.ImageButton", info.className)
            ) {
                Log.e("huangyuan", "找到了 通讯录 ,类型是--> ${info.className}")
                currentStep = Step_ClickVideoMenu
                info.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                currentStep = Step_FindVideoButton
                return
            }


            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }
        }
    }


//    -----------------------------------------------
//    -----------------------------------------------
//    -----------------------------------------------
//    -----------------------------------------------
//    -----------------------------------------------
//    -----------------------------------------------
//    -----------------------------------------------

    /**
     * 点击指定文案的节点
     * @param textList 选文案列表，按顺序查找，找到即停止查找
     * @param parentCount 0 表示点击查找到的节点，大于 0 表示点击向上查找指定层级的父节点
     */
    private fun AccessibilityNodeInfo.clickNodeByText(
        textList: Array<String>,
        parentCount: Int = 0
    ) {
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

        node?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
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


    private fun findVideoFunctionAndClick() {
        //先找到更多按钮，点击之后展开更多输入类型，点击视频通话，在底部弹窗中点击 视频通话

        var rootNodeInfo: AccessibilityNodeInfo? = null
        for (window in windows) {
            if (window.type == AccessibilityWindowInfo.TYPE_APPLICATION) {
                rootNodeInfo = window.root
            }
        }
        val root = rootNodeInfo ?: rootInActiveWindow


        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(root)
        var info: AccessibilityNodeInfo?
        while (!queue.isEmpty()) {
            info = queue.poll()
            if (info == null) {
                continue
            }
//            Log.e(
//                "huangyuan",
//                "nodeinfo--> ${info.className} , ${info.text} ,${info.viewIdResourceName} , ${info.contentDescription} "
//            )
            //更多功能按钮
            if (TextUtils.equals(info.text, "相册")) {
                Log.e("huangyuan", "找到了 视频通话按钮 ,类型是--> ${info.className}  ${info.text}")

                while (info != null) {
                    if (info.isClickable) {
                        Log.e("huangyuan", "isClickable is true")
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                    if (info.parent != null && info.parent.className.contains("GridView")) {
                        break
                    }
                    info = info.parent
                }
                if (info != null) {
                    visitNodeInfo(info)
                }


                return
            }


            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }
        }
    }


    private fun findVideoMenuAndClick() {
        //先找到更多按钮，点击之后展开更多输入类型，点击视频通话，在底部弹窗中点击 视频通话

        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(rootInActiveWindow)
        var info: AccessibilityNodeInfo?
        while (!queue.isEmpty()) {
            info = queue.poll()
            if (info == null) {
                continue
            }
            Log.e(
                "huangyuan",
                "nodeinfo--> ${info.className} , ${info.text} ,${info.viewIdResourceName} , ${info.contentDescription} ,${info.isVisibleToUser}"
            )
            //更多功能按钮
            if (TextUtils.equals(
                    info.contentDescription,
                    "更多功能按钮，已折叠"
                ) && TextUtils.equals("android.widget.ImageButton", info.className)
            ) {
                Log.e("huangyuan", "找到了 +按钮 ,类型是--> ${info.className}")
                currentStep = Step_ClickVideoMenu
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                currentStep = Step_FindVideoButton
                return
            }


            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }
        }

    }


    private fun findSearchButtonAndClick() {
        Log.e("huangyuan", "开始寻找搜索按钮")
        val nodeInfo = getRootNodeInfo()?.getNodeByContentDescription("搜索")
        if (nodeInfo != null) {
            Log.e("huangyuan", "找到搜索按钮，开始点击")
            currentStep = Step_ClickSearchButton
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            currentStep = Step_FindSearchInputText

        } else {
            Log.e("huangyuan", "没有找到搜索按钮")
        }
    }

    private fun findSearchEditTextAndInput() {
        Log.e("huangyuan", "开始寻找搜索框，并进行输入")
        val nodeInfo = getRootNodeInfo()?.getNodeByCustomText("搜索")
        if (nodeInfo == null) {
            Log.e("huangyuan", "没有找到 搜索 相关控件")
        } else {
            if (nodeInfo.className.contains("EditText")) {
                Log.e("huangyuan", "开始输入")
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                val arguments = Bundle()
                arguments.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    USER_NAME
                )
                nodeInfo.performAction(
                    AccessibilityNodeInfo.ACTION_SET_TEXT,
                    arguments
                )
                currentStep = Step_FindContact
                findContactAndClick()

            } else {
                Log.e("huangyuan", "没有找到 搜索编辑框 相关控件")
            }

        }
    }


    // 当窗口发生的事件是我们配置监听的事件时,会回调此方法.会被调用多次
    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        if (event.packageName == WX_PACKAGE_NAME && (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)) {
            Log.e("huangyuan", "微信活动 当前步骤--> $currentStep")
            when (currentStep) {
                Step_Start -> {
                    handler.removeMessages(Step_Start)
                    handler.sendEmptyMessageDelayed(Step_Start, 500)
                }


                Step_FindSearchInputText -> {
                    handler.removeMessages(Step_FindSearchInputText)
                    handler.sendEmptyMessageDelayed(Step_FindSearchInputText, 500)

                }

                Step_FindVideoMenu -> {
                    handler.removeMessages(Step_FindVideoMenu)
                    handler.sendEmptyMessageDelayed(Step_FindVideoMenu, 500)
                }

                Step_FindVideoButton -> {
                    handler.removeMessages(Step_FindVideoButton)
                    handler.sendEmptyMessageDelayed(Step_FindVideoButton, 1000)
                }

                Step_search_contact -> {

                    handler.removeMessages(Step_search_contact)
                    handler.sendEmptyMessageDelayed(Step_search_contact, 2000)
                }

                Step_search_and_click_video->{
                    handler.removeMessages(Step_search_and_click_video)
                    handler.sendEmptyMessageDelayed(Step_search_and_click_video, 2000)
                }

                Step_start_video_chat->{
                    handler.removeMessages(Step_start_video_chat)
                    handler.sendEmptyMessageDelayed(Step_start_video_chat, 2000)
                }

                else -> {
                    Log.e("huangyuan", "当前步骤未进行 $currentStep")
                }
            }
        }
    }


    private fun findContactAndClick() {
        Log.e("huangyuan", "开始查找联系人")
        EasyTimer.schedule(800) {

            val queue = LinkedList<AccessibilityNodeInfo>()
            queue.offer(rootInActiveWindow)
            var info: AccessibilityNodeInfo?
            while (!queue.isEmpty()) {
                info = queue.poll()
                if (info == null) {
                    continue
                }
//                Log.e(
//                    "huangyuan",
//                    "nodeinfo--> ${info.className} , ${info.text} ,${info.viewIdResourceName} , ${info.contentDescription} ,${info.isVisibleToUser}"
//                )
                if (TextUtils.equals(
                        info.text,
                        USER_NAME
                    ) && TextUtils.equals("android.widget.TextView", info.className)
                ) {
                    Log.e("huangyuan", "找到了 $USER_NAME ,类型是--> ${info.className}")
                    Log.e("huangyuan", "找到了,开始点击")
                    currentStep = Step_ClickContact
//
//
                    while (info != null) {
                        Log.e(
                            "huangyuan",
                            "parent info ${info.text} , ${info.className} , ${info.contentDescription}"
                        )
                        if (info.className.contains("RelativeLayout")) {
                            info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            currentStep = Step_FindVideoMenu
                            info = null
                        } else {
                            info = info.parent
                        }
//

                    }


                    return@schedule

                }


                for (i in 0 until info.childCount) {
                    queue.offer(info.getChild(i))
                }
            }


        }
    }


    private fun AccessibilityNodeInfo.getNodeByCustomText(text: String): AccessibilityNodeInfo? {
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(this)
        var info: AccessibilityNodeInfo?
        while (!queue.isEmpty()) {
            info = queue.poll()
            if (info == null) {
                continue
            }
//            Log.e(
//                "huangyuan",
//                "nodeinfo--> ${info.className} , ${info.text} ,${info.viewIdResourceName} , ${info.contentDescription} ,${info.isVisibleToUser}"
//            )
            if (TextUtils.equals(info.text, text)) {
                Log.e("huangyuan", "找到了 $text ,类型是--> ${info.className}")
                return info
            }

            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }
        }
        return null
    }


    private fun AccessibilityNodeInfo.getNodeByContentDescription(desc: String): AccessibilityNodeInfo? {
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(this)
        var info: AccessibilityNodeInfo?
        while (!queue.isEmpty()) {
            info = queue.poll()
            if (info == null) {
                continue
            }
//            Log.e(
//                "huangyuan",
//                "nodeinfo--> ${info.className} , ${info.text} ,${info.viewIdResourceName} , ${info.contentDescription} ,${info.isVisibleToUser}"
//            )
            if (info.contentDescription != null && info.contentDescription == desc) {
                Log.e("huangyuan", "找到了 $desc ,类型是--> ${info.className}")
                return info
            }

            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }
        }
        return null
    }


    // 获取 APPLICATION 的 Window 根节点
    private fun getRootNodeInfo(): AccessibilityNodeInfo {
        return rootInActiveWindow
    }

    override fun onInterrupt() {
        //当服务要被中断时调用.会被调用多次
    }

}