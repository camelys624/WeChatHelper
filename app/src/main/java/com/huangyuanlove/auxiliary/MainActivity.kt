package com.huangyuanlove.auxiliary

import android.content.DialogInterface
import android.content.Intent
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
import com.huangyuanlove.auxiliary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

   lateinit var  binding:ActivityMainBinding

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

    }

    override fun onResume() {
        super.onResume()
        if(!isAccessibilitySettingOn()){
          AlertDialog.Builder(this)
              .setTitle("开启辅助功能")
              .setNegativeButton("去开启",object:DialogInterface.OnClickListener{
                  override fun onClick(dialog: DialogInterface?, which: Int) {
                     startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                  }

              })
              .show()
        }

    }

    fun isAccessibilitySettingOn():Boolean{

        var accessibilityEnabled = 0
       try {
           accessibilityEnabled =  Settings.Secure.getInt(contentResolver,Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
       }catch (e:Exception){
           e.printStackTrace()
       }

        if(accessibilityEnabled == 1){
            try {
                val services = Settings.Secure.getString(contentResolver,Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                services?.let {
                    return services.lowercase().contains(packageName.lowercase())
                }
            }catch (e:Exception ){
                e.printStackTrace()
            }

        }



        return false
    }

}