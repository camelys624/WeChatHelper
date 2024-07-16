package com.huangyuanlove.auxiliary.base

import android.app.ActionBar
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.huangyuanlove.auxiliary.R

open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.let {
            val actionBarView = layoutInflater.inflate(R.layout.view_action_bar,null)
            actionBarView.findViewById<View>(R.id.back).setOnClickListener{
                onBackPressedDispatcher.onBackPressed()
            }
            it.customView = actionBarView
            it.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

        }
    }

    override fun setTitle(title: CharSequence?) {
       supportActionBar?.customView?.findViewById<TextView>(R.id.title)?.text = title
    }

    override fun setTitle(titleId: Int) {
        super.setTitle(titleId)
        supportActionBar?.customView?.findViewById<TextView>(R.id.title)?.text = getText(titleId)
    }
}