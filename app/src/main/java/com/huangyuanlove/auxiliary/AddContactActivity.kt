package com.huangyuanlove.auxiliary

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hjq.toast.Toaster
import com.huangyuanlove.auxiliary.bean.Contact
import com.huangyuanlove.auxiliary.databinding.ActivityAddContactBinding
import com.huangyuanlove.auxiliary.databinding.ActivityMainBinding

class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private var avatar:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.save.setOnClickListener{

        }


    }

    private fun save(){
        if(TextUtils.isEmpty(avatar)){
            Toaster.show("请选择头像")
           return
        }
        val wechatMark = binding.wechatMark.text.toString().trim()
        if(TextUtils.isEmpty(wechatMark)){
            Toaster.show("请输入微信昵称")
            return
        }
        val phone = binding.phone.text.toString().trim()
        if(TextUtils.isEmpty(phone)){
            Toaster.show("请输入手机号码")
            return
        }

        val contact = Contact()
        contact.last =false
        contact.name = wechatMark
        contact.phone = phone
        contact.avatar = avatar


    }

}