package com.kreipikc.activitycenter.presentation.ui.settings

import android.content.Intent
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.kreipikc.activitycenter.R
import com.kreipikc.activitycenter.databinding.ActivityMainBinding
import com.kreipikc.activitycenter.presentation.ui.base.BaseActivity
import com.kreipikc.activitycenter.presentation.ui.main.MainActivity

class SettingsActivity : BaseActivity<ActivityMainBinding>() {
    override fun getLayoutRes(): Int = R.layout.activity_settings

    override fun setupContent() {
        setupClickEvents()
    }

    override fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    private fun setupClickEvents() {
        findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            findViewById<DrawerLayout>(R.id.main).openDrawer(GravityCompat.START)
        }
    }
}