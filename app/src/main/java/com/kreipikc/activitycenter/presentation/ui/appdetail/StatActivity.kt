package com.kreipikc.activitycenter.presentation.ui.appdetail

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kreipikc.activitycenter.R
import com.kreipikc.activitycenter.domain.utils.IconLoader

class StatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        setupView()
        setupBtnClick()
    }

    private fun setupView() {
        val packageName = intent.getStringExtra("packageName") ?: this.getString(R.string.unknown_app)

        findViewById<TextView>(R.id.textName).text = intent.getStringExtra("appName")
        findViewById<TextView>(R.id.textPackageName).text = packageName
        findViewById<TextView>(R.id.allTextTimeDetail).text = intent.getStringExtra("usageTime")
        findViewById<TextView>(R.id.lastTextTimeDetail).text = intent.getStringExtra("lastUsedTime")
        findViewById<ImageView>(R.id.appIconDetail).setImageDrawable(IconLoader.loadAppIcon(this, packageName))
    }

    private fun setupBtnClick() {
        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
}