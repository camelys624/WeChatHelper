package com.huangyuanlove.auxiliary

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.Toaster
import com.huangyuanlove.auxiliary.bean.Contact
import com.huangyuanlove.auxiliary.databinding.ActivityAddContactBinding
import com.huangyuanlove.auxiliary.utils.ObjectBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private var avatar: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolBar
        toolbar.title = "添加联系人"
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.save.setOnClickListener {
            save()
        }

        val pickImageLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result.data?.data
                    imageUri?.let {
                        avatar = copyToInternal(it)
                        if (avatar.isEmpty()) {
                            Toaster.show("图片使用失败，请重新选择")
                        } else {
                            Glide.with(this).load(avatar)
                                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(8)))
                                .into(binding.avatar)
                        }

                    }

                }
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
                        pickImageLaunch.launch(intent)
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean
                    ) {
                        if (doNotAskAgain) {
                            Toaster.show("被永久拒绝授权，请手动授予权限")
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(
                                this@AddContactActivity,
                                permissions
                            )
                        } else {
                            Toaster.show("获取权限失败")
                        }
                    }
                })
        }
        binding.wechatMarkTip.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("昵称提示")
                .setMessage("请避免使用单个英文字母作为微信昵称或备注，如果微信昵称为单个英文字母，可使用备注的形式修改展示在微信联系人列表中的名字")
                .setPositiveButton(
                    "我知道了"
                ) { dialog, which -> dialog?.dismiss() }
                .create().show()
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
        if(wechatMark.length == 1){
            val singleChar = wechatMark.toCharArray()[0]
            if(singleChar in 'a'..'z'){
                Toaster.show("不能使用单个字母作为昵称")
                return
            }
            if(singleChar in 'A'..'Z'){
                Toaster.show("不能使用单个字母作为昵称")
                return
            }
        }

        val phone = binding.phone.text.toString().trim()
        if (TextUtils.isEmpty(phone)) {
            Toaster.show("请输入手机号码")
            return
        }




        if (phone.length != 11) {
            AlertDialog.Builder(this)
                .setTitle("电话号码提示")
                .setMessage("电话号码不是 11 位数字，请确认")
                .setNegativeButton("重新输入") { dialog, _ ->
                    dialog?.dismiss()
                }
                .setPositiveButton(
                    "保存"
                ) { dialog, _ ->
                    dialog?.dismiss()
                    saveAndFinish()
                }
                .create().show()
        } else {
            saveAndFinish()
        }
    }


    private fun saveAndFinish() {
        val contact = Contact()
        contact.last = false
        contact.name = binding.wechatMark.text.toString().trim()
        contact.phone = binding.phone.text.toString().trim()
        contact.avatar = avatar
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                ObjectBox.sotre.boxFor(Contact::class.java).put(contact)
            }
            setResult(Activity.RESULT_OK)
            finish()

        }
    }


    private fun copyToInternal(uri: Uri): String {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, System.currentTimeMillis().toString())
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.flush()
            outputStream.close()

            return file.absolutePath
        } catch (e: Exception) {
            e.toString()
        }
        return ""

    }

}