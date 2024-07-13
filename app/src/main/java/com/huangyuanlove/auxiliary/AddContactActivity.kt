package com.huangyuanlove.auxiliary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.Toaster
import com.huangyuanlove.auxiliary.bean.Contact
import com.huangyuanlove.auxiliary.databinding.ActivityAddContactBinding
import com.huangyuanlove.auxiliary.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private var avatar: String = ""

    private val REQUEST_CODE_PICK_IMAGE = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.save.setOnClickListener {
            save()
        }
        binding.avatar.setOnClickListener {
            XXPermissions.with(this@AddContactActivity).permission(Permission.READ_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {

                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (!allGranted) {
                            Toaster.show("获取部分权限成功，但部分权限未正常授予")
                            return
                        }
                        Toaster.show("获取权限成功")
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
                    }

                    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                        if (doNotAskAgain) {
                            Toaster.show("被永久拒绝授权，请手动授予权限")
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(this@AddContactActivity, permissions)
                        } else {
                            Toaster.show("获取权限失败")
                        }
                    }
                })
        }


    }

    private fun save() {
        if (TextUtils.isEmpty(avatar)) {
            Toaster.show("请选择头像")
            return
        }
        val wechatMark = binding.wechatMark.text.toString().trim()
        if (TextUtils.isEmpty(wechatMark)) {
            Toaster.show("请输入微信昵称")
            return
        }
        val phone = binding.phone.text.toString().trim()
        if (TextUtils.isEmpty(phone)) {
            Toaster.show("请输入手机号码")
            return
        }

        val contact = Contact()
        contact.last = false
        contact.name = wechatMark
        contact.phone = phone
        contact.avatar = avatar
        lifecycleScope.launch {
            withContext(Dispatchers.IO){

            }
//            setResult(Activity.RESULT_OK)
//            finish()
        }




    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            avatar = imageUri.toString()
            binding.avatar.setImageURI(imageUri) // imageView是您用来显示图片的ImageView组件
        }
    }

}