package com.kreipikc.activitycenter.presentation.ui.main

import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kreipikc.activitycenter.R
import com.kreipikc.activitycenter.data.datasource.SystemUsageDataSource
import com.kreipikc.activitycenter.domain.model.AppUsageInfo
import com.kreipikc.activitycenter.presentation.adapter.StatsAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var appUsageActivity: SystemUsageDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        appUsageActivity = SystemUsageDataSource(this)

        checkPermissionStatus()
        setupClickEvents()
    }

    override fun onResume() {
        super.onResume()
        checkPermissionStatus()
    }

    private fun setupClickEvents() {
        findViewById<Button>(R.id.refreshButton).setOnClickListener {
            loadAppUsageStats()
        }
    }

    private fun checkPermissionStatus() {
        val statusText = findViewById<TextView>(R.id.statusText)
        if (hasUsageStatsPermission()) {
            statusText.text = getString(R.string.access_granted)
            loadAppUsageStats()
        } else {
            statusText.text = getString(R.string.no_access)
            askForUsageStatsPermission()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun loadAppUsageStats() {
        val stats = appUsageActivity.getAppUsageLast24Hours()
        displayStats(stats)
    }

    private fun displayStats(stats: List<AppUsageInfo>) {
        val listStatsItem = findViewById<RecyclerView>(R.id.listStatsItem)

        listStatsItem.layoutManager = LinearLayoutManager(this)
        listStatsItem.adapter = StatsAdapter(stats, this)
    }

    private fun askForUsageStatsPermission() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.required_permissions))
            .setMessage(getString(R.string.permission_explanation).trimIndent())
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> finishAffinity() }
            .show()
    }
}