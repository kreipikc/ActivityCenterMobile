package com.kreipikc.activitycenter.data.datasource

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import com.kreipikc.activitycenter.domain.model.AppUsageInfo
import java.util.Calendar

class SystemUsageDataSource(private val context: Context) {
    private lateinit var usageStatsManager : UsageStatsManager

    fun getAppUsageLast24Hours(): List<AppUsageInfo> {
        usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()

        val endTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        return processUsageStats(getStatOnRangeTime(startTime, endTime))
    }

    private fun getStatOnRangeTime(startTime: Long, endTime: Long): MutableList<UsageStats> {
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        return usageStats
    }

    private fun processUsageStats(usageStats: List<UsageStats>): List<AppUsageInfo> {
        val appUsageList = mutableListOf<AppUsageInfo>()

        for (stats in usageStats) {
            if (stats.totalTimeInForeground > 0) {
                val usageInfo = AppUsageInfo(
                    packageName = stats.packageName,
                    appName = getAppName(stats.packageName),
                    usageTime = stats.totalTimeInForeground,
                )
                appUsageList.add(usageInfo)
            }
        }

        return appUsageList.sortedByDescending { it.usageTime }
    }

    private fun getAppName(packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }
}