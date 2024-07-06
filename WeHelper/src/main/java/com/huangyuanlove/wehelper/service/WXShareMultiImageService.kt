package com.huangyuanlove.wehelper.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import android.widget.EditText
import android.widget.Toast
import com.huangyuanlove.wehelper.ShareInfo
import com.huangyuanlove.wehelper.constant.WX_PACKAGE_NAME
import com.huangyuanlove.wehelper.utils.EasyTimer
import java.util.*
import kotlin.math.log

private const val LAUNCHER_UI = "com.tencent.mm.ui.LauncherUI"
private const val TIMELINE_UI = "com.tencent.mm.plugin.sns.ui.SnsTimeLineUI"
private const val UPLOAD_UI = "com.tencent.mm.plugin.sns.ui.SnsUploadUI"
private const val ALBUM_PREVIEW_UI = "com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI"

private val DISCOVER_TEXT_LIST = arrayOf("发现", "Discover")
private val TIMELINE_TEXT_LIST = arrayOf("朋友圈", "Moments")
private val SHARE_TEXT_LIST = arrayOf("拍照分享", "")
private val SELECT_FROM_ALBUM_TEXT_LIST = arrayOf(
    "从相册选择", "Select Photos or Videos from Album", "Choose from Album"
)
private val POST_TEXT_LIST = arrayOf("发表", "Post")

enum class Step {
    Idle, Launcher, Discover, Timeline, PrepareAlbum, AlbumPreview, Upload
}

/**
 * Created by StoneHui on 2018/10/22.
 * <p>
 * 微信多图分享服务。
 */
class WXShareMultiImageService : AccessibilityService() {

    // GridView 或者 RecyclerView
    private val gvOrRcvRegex = "(?:\\.GridView|\\.RecyclerView)$".toRegex()

    // EditText
    private val etRegex = EditText::class.java.name.toRegex()

    // View 或者 CheckBox
    private val vOrCbRegex = "(?:\\.View|\\.CheckBox)$".toRegex()

    // Button
    private val btnRegex = "(?:\\.AppCompatButton|\\.Button)$".toRegex()

    // 当前步骤
    private var step = Step.Launcher

    // 当窗口发生的事件是我们配置监听的事件时,会回调此方法.会被调用多次
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        //遍历当前页面所有节点，寻找合适节点进行点击

//        if(event.packageName == WX_PACKAGE_NAME && (event.eventType ==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.eventType ==AccessibilityEvent.TYPE_VIEW_CLICKED) ){
//            getRootNodeInfo()?.visitAll();
//        }
        if (event.packageName == WX_PACKAGE_NAME) {
            Log.e("huangyuan", "eventType--> ${event.eventType}")
        }
        if (event.packageName == WX_PACKAGE_NAME && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.e("huangyuan", "开始寻找")
            val nodeInfo = getRootNodeInfo()?.getNodeByContentDescription("搜索")
            if (nodeInfo != null) {
                Log.e("huangyuan", "找到搜索按钮")
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            } else {
                Log.e("huangyuan", "没有找到搜索按钮")
            }

        }



        if (!ShareInfo.options.isAutoFill) {
            return
        }
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                when (event.className.toString()) {
                    LAUNCHER_UI -> processLauncherUI()
                    TIMELINE_UI -> prepareGoIntoAlbum()
                    ALBUM_PREVIEW_UI -> selectImage()
                    UPLOAD_UI -> processingUploadUI()
                    else -> {
                    }
                }
            }

            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                when (step) {
                    Step.Discover -> goIntoTimeline()
                    Step.PrepareAlbum -> goIntoAlbum()
                    else -> {
                    }
                }
            }

            else -> {
            }
        }

    }

    // 处理启动页
    private fun processLauncherUI() {
        if (ShareInfo.waitingImageCount <= 0) {
            step = Step.Idle
        } else {
            step = Step.Launcher
            goIntoDiscover()
        }
    }

    // 进入发现页
    private fun goIntoDiscover() {
        if (step !== Step.Launcher) {
            return
        }
        EasyTimer.schedule(200) {
            step = Step.Discover
            getRootNodeInfo()?.clickNodeByText(DISCOVER_TEXT_LIST, 2)
        }
    }

    // 进入朋友圈
    private fun goIntoTimeline() {
        if (step !== Step.Discover) {
            return
        }
        EasyTimer.schedule(200) {
            step = Step.Timeline
            getRootNodeInfo()?.clickNodeByText(TIMELINE_TEXT_LIST, 8)
        }
    }

    // 准备进入相册
    private fun prepareGoIntoAlbum() {
        if (step !== Step.Timeline) {
            return
        }
        EasyTimer.schedule(200) {
            step = Step.PrepareAlbum
            getRootNodeInfo()?.clickNodeByText(SHARE_TEXT_LIST)
        }
    }

    // 进入相册
    private fun goIntoAlbum() {
        if (step !== Step.PrepareAlbum) {
            return
        }
        EasyTimer.schedule(200) {
            step = Step.AlbumPreview
            getRootNodeInfo()?.clickNodeByText(SELECT_FROM_ALBUM_TEXT_LIST, 3)
        }
    }

    // 选择图片
    private fun selectImage() {
        if (step !== Step.AlbumPreview) {
            return
        }
        EasyTimer.schedule(200) {
            val rootNodeInfo = getRootNodeInfo() ?: return@schedule
            val targetView = rootNodeInfo.getChild(gvOrRcvRegex) ?: return@schedule
            // 选图
            val maxIndex = ShareInfo.selectedImageCount + ShareInfo.waitingImageCount - 1
            (ShareInfo.selectedImageCount..maxIndex).forEach {
                targetView.getChild(it).clickNodeByClassName(vOrCbRegex)
            }
            // 选图结束
            selectImageFinished()
        }
    }

    // 选择图片完成
    private fun selectImageFinished() {
        ShareInfo.setImageCount(0, 0)
        EasyTimer.schedule(200) {
            step = Step.Upload
            getRootNodeInfo()?.clickNodeByClassName(btnRegex)
        }
    }

    // 处理图文分享界面
    private fun processingUploadUI() {
        if (step !== Step.Upload) {
            return
        }
        val rootNodeInfo = getRootNodeInfo()
        if (rootNodeInfo === null) {
            step = Step.Idle
            return
        }
        // 粘贴待分享文案
        if (ShareInfo.options.text.isNotEmpty()) {
            ShareInfo.options.text = ""
            rootNodeInfo.getChild(etRegex)?.run {
                performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                performAction(AccessibilityNodeInfo.ACTION_PASTE)
            }
        }
        // 发表
        if (ShareInfo.options.isAutoPost) {
            ShareInfo.options.isAutoPost = false
            EasyTimer.schedule(200) {
                rootNodeInfo.clickNodeByText(POST_TEXT_LIST)
            }
        }
        step = Step.Idle
    }

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
        repeat(parentCount) {
            node = node?.parent
        }
        node?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    /**
     * 点击指定类名的节点
     * @param className 类名正则
     */
    private fun AccessibilityNodeInfo.clickNodeByClassName(className: Regex) {
        this.getChild(className)?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
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


    private fun AccessibilityNodeInfo.getNodeByContentDescription(contentDescription: String): AccessibilityNodeInfo? {
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(this)
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
            info.contentDescription?.let {
                if(it.contains(contentDescription)){
                    Log.e("huangyuan", "找到搜索按钮")
                    return info
                }
            }

            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }
        }
        return null
    }

    private fun AccessibilityNodeInfo.visitAll() {
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(this)
        var info: AccessibilityNodeInfo?
        while (!queue.isEmpty()) {
            info = queue.poll()
            if (info == null) {
                continue
            }
//            Log.e("huangyuan","nodeinfo--> ${ info.className} , ${info.text} ,${info.viewIdResourceName} , ${info.contentDescription} ,${info.isVisibleToUser}")
            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }
        }
    }

    /**
     * 查找指定类名的节点
     * @param className 类名正则
     * @return 对应的节点或者 null
     */
    private fun AccessibilityNodeInfo.getChild(className: Regex): AccessibilityNodeInfo? {
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.offer(this)
        var info: AccessibilityNodeInfo?
        while (!queue.isEmpty()) {
            info = queue.poll()
            if (info == null) {
                continue
            }
            if (info.className.toString().contains(className)) {
                return info
            }
            for (i in 0 until info.childCount) {
                queue.offer(info.getChild(i))
            }
        }
        return null
    }

    // 获取 APPLICATION 的 Window 根节点
    private fun getRootNodeInfo(): AccessibilityNodeInfo? {
        var rootNodeInfo: AccessibilityNodeInfo? = null
        for (window in windows) {
            if (window.type == AccessibilityWindowInfo.TYPE_APPLICATION) {
                rootNodeInfo = window.root
            }
        }
        return rootNodeInfo ?: rootInActiveWindow
    }

    override fun onInterrupt() {
        //当服务要被中断时调用.会被调用多次
    }

}