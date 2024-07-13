package com.huangyuanlove.auxiliary

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.app.DialogCompat
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
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
import com.huangyuanlove.wehelper.WXShareMultiImageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactAdapter: ContactAdapter
    private val contactList = ArrayList<Contact>()

    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.launchWechat.setOnClickListener {
            val intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
            intent?.let {
                startActivity(intent)
            }
        }
        binding.insert.setOnClickListener {
            val contextBox = ObjectBox.sotre.boxFor(Contact::class.java)
            val contact = Contact()
            contact.last = false
            contact.name = "test"
            contact.phone = "11111111111"
            contact.avatar = ""
            contextBox.put(contact)
            Toaster.show("插入成功")

        }
        binding.query.setOnClickListener {
            val contactBox = ObjectBox.sotre.boxFor(Contact::class.java)

            val result = contactBox.all
            for (contact in result) {
                Log.e("MainActivity", contact.toString())
            }
        }


        binding.contactRv.layoutManager = GridLayoutManager(this, 2)
        binding.contactRv.addItemDecoration(GridSpacingItemDecoration(2, 45, true))
        contactList.addAll(ObjectBox.sotre.boxFor(Contact::class.java).all)
        contactList.add(createLastAddContact())
        contactAdapter = ContactAdapter(contactList)

        contactAdapter.onItemClick = object : OnItemClick {
            override fun onItemClick(contact: Contact) {
                if (contact.last) {
                    startActivityForResult(Intent(this@MainActivity, AddContactActivity::class.java), 10085)
                } else {
                    showMakeCallDialog(contact)
                }


            }
        }

        binding.contactRv.adapter = contactAdapter

        contactAdapter.notifyDataSetChanged()

    }


    private fun isAccessibilitySettingOn(): Boolean {
        return WXShareMultiImageHelper.isServiceEnabled(this)

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
                val intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
                intent?.let {
                    startActivity(intent)
                }
            }
        }
        view.findViewById<View>(R.id.wechat_phone).setOnClickListener {
            if(checkAccessibility()){

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10085 && resultCode == Activity.RESULT_OK && data != null) {
            contactList.clear()
            val result = ObjectBox.sotre.boxFor(Contact::class.java).all
            contactList.addAll(result)
            contactList.add(createLastAddContact())
            log(contactList.size.toString())
            log(contactList.toString())
            contactAdapter.notifyDataSetChanged()
        }
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