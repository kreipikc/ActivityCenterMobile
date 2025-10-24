package com.kreipikc.activitycenter.presentation.ui.appdetail

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kreipikc.activitycenter.R

class StatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        setupView()
        setupBtnClick()
    }

    private fun setupView() {
        findViewById<TextView>(R.id.textName).text = intent.getStringExtra("appName")
        findViewById<TextView>(R.id.textPackageName).text = intent.getStringExtra("packageName")
        findViewById<TextView>(R.id.allTextTimeDetail).text = intent.getStringExtra("usageTime")
        findViewById<TextView>(R.id.lastTextTimeDetail).text = intent.getStringExtra("lastUsedTime")
    }

    private fun setupBtnClick() {
        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
}