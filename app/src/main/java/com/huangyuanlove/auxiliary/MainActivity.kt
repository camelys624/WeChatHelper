package com.huangyuanlove.auxiliary

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings
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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.huangyuanlove.auxiliary.bean.Contact
import com.huangyuanlove.auxiliary.bean.ContactRepository
import com.huangyuanlove.auxiliary.databinding.ActivityMainBinding
import com.huangyuanlove.wehelper.WXShareMultiImageHelper

class MainActivity : AppCompatActivity() {

   private lateinit var  binding:ActivityMainBinding
   private lateinit var contactAdapter:ContactAdapter
   private lateinit var contactRepository: ContactRepository
   private val contactList = ArrayList<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactRepository= ContactRepository()

        binding.launchWechat.setOnClickListener {
            val intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
            intent?.let {
                startActivity(intent)
            }
        }
        binding.shareToWechatSession.setOnClickListener {
            WXShareMultiImageHelper.shareToSession(this, arrayOf<Bitmap>())
        }
        binding.contactRv.layoutManager = GridLayoutManager(this,2)
        contactList.add(createLastAddContact())
        contactAdapter = ContactAdapter(contactList)

        contactAdapter.onItemClick = object :OnItemClick{
            override fun onItemClick(contact: Contact) {
                if(contact.last){
                    showAddContactDialog()
                }else{
                    showMakeCallDialog()
                }



            }
        }

        binding.contactRv.adapter = contactAdapter

        contactAdapter.notifyDataSetChanged()

    }

    override fun onResume() {
        super.onResume()
        if(!isAccessibilitySettingOn()){
          AlertDialog.Builder(this)
              .setTitle("开启辅助功能")
              .setNegativeButton("去开启"
              ) { _, _ -> startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
              .show()
        }

    }

    private fun isAccessibilitySettingOn():Boolean{
       return WXShareMultiImageHelper.isServiceEnabled(this)

    }

    private fun createLastAddContact():Contact{
        val contact = Contact()
        contact.last = true
        return contact
    }
    private fun showAddContactDialog(){
      startActivityForResult(Intent(this,AddContactActivity::class.java),10085)
    }
    private fun showMakeCallDialog(){
        val bottomSheetDialog = BottomSheetDialog(this)
        val view =layoutInflater.inflate(R.layout.view_contact_click_bottom_sheet_dialog,null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

}