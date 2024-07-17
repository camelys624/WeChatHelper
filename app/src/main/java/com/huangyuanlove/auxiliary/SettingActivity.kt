package com.huangyuanlove.auxiliary

import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.huangyuanlove.auxiliary.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        binding.toolBar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.sensitivity.setOnSeekBarChangeListener(object:OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }
}