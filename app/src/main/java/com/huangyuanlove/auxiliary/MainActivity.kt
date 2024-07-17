package com.huangyuanlove.auxiliary

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.Toaster
import com.huangyuanlove.auxiliary.bean.Contact
import com.huangyuanlove.auxiliary.databinding.ActivityMainBinding
import com.huangyuanlove.auxiliary.utils.ObjectBox
import com.huangyuanlove.wehelper.constant.WX_PACKAGE_NAME
import com.huangyuanlove.wehelper.utils.Action
import com.huangyuanlove.wehelper.utils.ServiceManager
import com.huangyuanlove.wehelper.utils.Step_Start
import com.huangyuanlove.wehelper.utils.WeChatCallStepManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactAdapter: ContactAdapter
    private val contactList = ArrayList<Contact>()

    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.setting.setOnClickListener{
            startActivity(Intent(this@MainActivity,SettingActivity::class.java))
        }
        binding.contactRv.layoutManager = GridLayoutManager(this, 2)
        binding.contactRv.addItemDecoration(GridSpacingItemDecoration(2, 45, true))
        contactList.addAll(ObjectBox.sotre.boxFor(Contact::class.java).all)
        contactList.add(createLastAddContact())
        contactAdapter = ContactAdapter(contactList)


        val launch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                contactList.clear()
                val tmp = ObjectBox.sotre.boxFor(Contact::class.java).all
                contactList.addAll(tmp)
                contactList.add(createLastAddContact())
                log(contactList.size.toString())
                log(contactList.toString())
                contactAdapter.notifyDataSetChanged()
            }
        }

        contactAdapter.onItemClick = object : OnItemClick {
            override fun onItemClick(contact: Contact) {
                if (contact.last) {
                    launch.launch(Intent(this@MainActivity, AddContactActivity::class.java),)
                } else {
                    showMakeCallDialog(contact)
                }


            }
        }

        binding.contactRv.adapter = contactAdapter

        contactAdapter.notifyDataSetChanged()

    }


    private fun isAccessibilitySettingOn(): Boolean {
        return ServiceManager.isServiceEnabled(this)

    }

    private fun createLastAddContact(): Contact {
        val contact = Contact()
        contact.last = true
        return contact
    }


    private fun showMakeCallDialog(contact: Contact) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.view_contact_click_bottom_sheet_dialog, null)

        view.findViewById<View>(R.id.wechat_video).setOnClickListener {
            if(checkAccessibility()){
                val intent = packageManager.getLaunchIntentForPackage(WX_PACKAGE_NAME)
                intent?.let {
                    WeChatCallStepManager.step = Step_Start
                    WeChatCallStepManager.action = Action.video_call
                    WeChatCallStepManager.name = contact.name
                    startActivity(intent)
                }
            }
        }
        view.findViewById<View>(R.id.wechat_phone).setOnClickListener {
            if(checkAccessibility()){
                val intent = packageManager.getLaunchIntentForPackage(WX_PACKAGE_NAME)
                intent?.let {
                    WeChatCallStepManager.step = Step_Start
                    WeChatCallStepManager.action = Action.voice_call
                    WeChatCallStepManager.name = contact.name
                    startActivity(intent)
                }
            }
        }
        view.findViewById<View>(R.id.make_phone_call).setOnClickListener {
            XXPermissions.with(this@MainActivity).permission(Permission.CALL_PHONE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (!allGranted) {
                            Toaster.show("获取部分权限成功，但部分权限未正常授予")
                            return
                        }
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.data = Uri.parse("tel:${contact.phone}")
                        startActivity(callIntent)

                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean
                    ) {
                        if (doNotAskAgain) {
                            Toaster.show("被永久拒绝授权，请手动授予权限")
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(
                                this@MainActivity,
                                permissions
                            )
                        } else {
                            Toaster.show("获取权限失败")
                        }
                    }

                })
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }



    private fun log(msg: String) {
        Log.e(tag, msg)
    }

    private fun checkAccessibility():Boolean{
        if (!isAccessibilitySettingOn()) {
            AlertDialog.Builder(this)
                .setTitle("开启辅助功能")
                .setNegativeButton(
                    "去开启"
                ) { _, _ -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                .show()
            return false

        }
        return true
    }


}

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}