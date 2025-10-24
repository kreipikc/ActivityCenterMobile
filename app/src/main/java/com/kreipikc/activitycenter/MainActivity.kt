package com.kreipikc.activitycenter

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
import com.kreipikc.activitycenter.data.datasource.SystemUsageDataSource
import com.kreipikc.activitycenter.domain.model.AppUsageInfo

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

    private fun setupClickEvents() {
        findViewById<Button>(R.id.permissionButton).setOnClickListener {
            askForUsageStatsPermission()
        }

        findViewById<Button>(R.id.refreshButton).setOnClickListener {
            loadAppUsageStats()
        }
    }

    private fun loadAppUsageStats() {
        if (hasUsageStatsPermission()) {
            val stats = appUsageActivity.getAppUsageLast24Hours()
            displayStats(stats)
        } else {
            findViewById<TextView>(R.id.statsTextView).text = "Сначала предоставьте доступ к статистике"
        }
    }

    private fun displayStats(stats: List<AppUsageInfo>) {
        val statsTextView = findViewById<TextView>(R.id.statsTextView)

        if (stats.isEmpty()) {
            statsTextView.text = "Нет данных об использовании приложений"
            return
        }

        val statsText = StringBuilder()
        statsText.append("📊 Статистика за день:\n\n")

        // Топ-10 приложений
        stats.take(10).forEachIndexed { index, appInfo ->
            val timeFormattedUsageTime = formatTime(appInfo.usageTime)
            statsText.append("${index + 1}. ${appInfo.appName}\n")
            statsText.append("   ⏱️ $timeFormattedUsageTime\n\n")
        }

        statsTextView.text = statsText.toString()
    }

    private fun formatTime(milliseconds: Long): String {
        val minutes = milliseconds / (1000 * 60)
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return if (hours > 0) {
            "${hours}ч ${remainingMinutes}м"
        } else {
            "${remainingMinutes}м"
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionStatus()
    }

    private fun askForUsageStatsPermission() {
        AlertDialog.Builder(this)
            .setTitle("Инструкция по поиску приложения")
            .setMessage("""
                ШАГИ:
                1. Нажми 'Открыть настройки'
                2. Найди 'Activity Center'
                3. Выдай права "Доступ к истории использования"
                4. После можешь возвращаться в приложение
            """.trimIndent())
            .setPositiveButton("Открыть настройки") { _, _ ->
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Отмена") { _, _ -> }
            .show()
    }

    private fun checkPermissionStatus() {
        val statusText = findViewById<TextView>(R.id.statusText)
        if (hasUsageStatsPermission()) {
            statusText.text = "✅ Доступ предоставлен"
            loadAppUsageStats()
        } else {
            statusText.text = "❌ Нет доступа к статистике"
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
}