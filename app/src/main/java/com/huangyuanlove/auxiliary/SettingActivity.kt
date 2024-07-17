package com.huangyuanlove.auxiliary

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.huangyuanlove.auxiliary.databinding.ActivitySettingBinding
import com.huangyuanlove.wehelper.constant.INVOKE_DAILY_TIME_MMKV_NAME
import com.huangyuanlove.wehelper.constant.INVOKE_DAILY_TIME_MULTIPLE
import com.tencent.mmkv.MMKV

class SettingActivity : AppCompatActivity() {



    private lateinit var binding:ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolBar.title = "设置"
        setSupportActionBar(binding.toolBar)
        binding.toolBar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.sensitivity.max = 19
        val progress = MMKV.mmkvWithID(INVOKE_DAILY_TIME_MMKV_NAME).getInt(INVOKE_DAILY_TIME_MULTIPLE,1)-1
        binding.sensitivity.progress =  progress
        binding.currentMultiple.text = "当前${progress+1}倍"
        binding.sensitivity.setOnSeekBarChangeListener(object:OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    MMKV.mmkvWithID(INVOKE_DAILY_TIME_MMKV_NAME).putInt(INVOKE_DAILY_TIME_MULTIPLE,progress+1)
                    binding.currentMultiple.text = "当前${progress+1}倍"
                Log.e("huangyuan","progress $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        binding.invokeTimeTip.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("执行时间提示")
                .setMessage("运行速度慢、卡顿的手机请调大此数值")
                .setPositiveButton("我知道了"){
                    dialog,_->dialog?.dismiss()
                }
                .show()
        }

    }
}