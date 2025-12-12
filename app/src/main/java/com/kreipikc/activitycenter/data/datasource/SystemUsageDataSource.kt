package com.kreipikc.activitycenter.data.datasource

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import com.kreipikc.activitycenter.domain.model.AppUsageInfo
import com.kreipikc.activitycenter.domain.utils.IconLoader
import java.util.Calendar

class SystemUsageDataSource(private val context: Context) {
    private var usageStatsManager : UsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    companion object {
        fun hasUsageStatsPermission(context: Context): Boolean {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
            return mode == AppOpsManager.MODE_ALLOWED
        }
    }

    fun getAppUsageLast24Hours(): List<AppUsageInfo> {
        val calendar = Calendar.getInstance()

        val endTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        return processUsageStats(getStatOnRangeTime(startTime, endTime))
    }

    fun getAppUsageYesterday(): List<AppUsageInfo> {
        val (startTime, endTime) = getYesterdayTimeRange()
        val usageStats = getStatOnRangeTime(startTime, endTime)
        return processUsageStats(usageStats)
    }

    private fun getYesterdayTimeRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_YEAR, -1)

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        return Pair(startTime, endTime)
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
        if (usageStats.isEmpty()) return emptyList()

        val groupedResults: List<AppUsageInfo> = usageStats
            .filter { it.totalTimeInForeground > 0 }
            .groupBy { it.packageName }
            .map { (packageName, statsForPackage) ->
                val totalTime = statsForPackage.sumOf { it.totalTimeInForeground }
                val lastUsed = statsForPackage.maxOf { it.lastTimeUsed }

                AppUsageInfo(
                    packageName = packageName,
                    appName = getAppName(packageName),
                    usageTime = totalTime,
                    lastUsedTime = lastUsed,
                    icon = IconLoader.loadAppIcon(context, packageName)
                )
            }

        return groupedResults.sortedByDescending { it.usageTime }
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