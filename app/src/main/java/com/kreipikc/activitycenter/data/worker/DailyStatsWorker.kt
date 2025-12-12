package com.kreipikc.activitycenter.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kreipikc.activitycenter.App
import com.kreipikc.activitycenter.data.datasource.SystemUsageDataSource
import com.kreipikc.activitycenter.domain.model.AppUsageInfo
import java.text.SimpleDateFormat
import java.util.*

class DailyStatsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "daily_stats_worker"
        private const val TAG = "DailyStatsWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "DailyStatsWorker started at ${Date()}")

        return try {
            if (!hasUsageStatsPermission()) {
                Log.w(TAG, "No usage stats permission")
                return Result.failure() // Не повторяем, т.к. без разрешения бессмысленно
            }

            val systemUsage = SystemUsageDataSource(applicationContext)
            val stats = systemUsage.getAppUsageYesterday()

            saveStatsToDatabase(stats)

            Log.d(TAG, "Worker completed successfully. Saved ${stats.size} records.")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Worker failed: ${e.message}", e)
            Result.failure()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        return SystemUsageDataSource.hasUsageStatsPermission(applicationContext)
    }

    private suspend fun saveStatsToDatabase(
        stats: List<AppUsageInfo>
    ) {
        for (stat in stats) {
            // Получаем или создаем приложение
            val app = App.appUsageRepository.getOrCreateApp(
                packageName = stat.packageName,
                displayName = stat.appName
            )
            Log.d(TAG, "App load into DB!")

            // Сохраняем использование
            val usageSeconds = stat.usageTime / 1000
            val yesterday = getYesterdayDate()
            App.appUsageRepository.saveUsage(app.id, usageSeconds, yesterday)
            Log.d(TAG, "UsageData load into DB!")
        }
    }

    private fun getYesterdayDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(calendar.time)
    }
}