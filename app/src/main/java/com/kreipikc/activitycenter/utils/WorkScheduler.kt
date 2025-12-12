package com.kreipikc.activitycenter.utils

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import com.kreipikc.activitycenter.data.worker.DailyStatsWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val WORK_TAG = "daily_stats_work"

    /**
     * Планирует ежедневный запуск Worker в 00:05
     */
    fun scheduleDailyStatsCollection(context: Context) {
        Log.d(WORK_TAG, "Запуск в очередь Worker'а")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()

        val initialDelay = calculateDelayToNextZeroFive()

        val dailyRequest = PeriodicWorkRequestBuilder<DailyStatsWorker>(
            24, TimeUnit.HOURS,    // Каждые 24 часа
            1, TimeUnit.HOURS    // Гибкое окно: можно выполнить в течение часа
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(WORK_TAG)
            .build()

        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniquePeriodicWork(
            DailyStatsWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, // Если уже есть задача - обновляем
            dailyRequest
        )
    }

    /**
     * Рассчитывает задержку до следующего 00:05
     */
    private fun calculateDelayToNextZeroFive(): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 5)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val targetTime = calendar.timeInMillis
        return targetTime - now
    }

    /**
     * Запускает Worker немедленно для тестирования
     */
    fun scheduleNowForTesting(context: Context) {
        Log.d("test_$WORK_TAG", "Запускаем тестовый Worker немедленно")

        val testRequest = OneTimeWorkRequestBuilder<DailyStatsWorker>()
            .setInitialDelay(0, TimeUnit.SECONDS)
            .addTag("test_$WORK_TAG")
            .build()

        val workManager = WorkManager.getInstance(context)

        workManager.enqueue(testRequest)
        workManager.getWorkInfoByIdLiveData(testRequest.id)
            .observeForever { workInfo ->
                Log.d("test_$WORK_TAG", "Состояние Worker: ${workInfo?.state}")
            }

        Log.d("test_$WORK_TAG", "Worker ID: ${testRequest.id}")
    }

    /**
     * Отменяет все запланированные задачи
     */
    fun cancelAllWork(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(WORK_TAG)
        workManager.cancelAllWorkByTag("test_$WORK_TAG")
    }

    /**
     * Проверяет, запланирована ли задача
     */
    fun isWorkerScheduled(context: Context): Boolean {
        val workManager = WorkManager.getInstance(context)
        // Эта часть сложнее, нужно использовать LiveData/Flow
        // Пока просто возвращаем true, если WorkManager инициализирован
        return true
    }
}