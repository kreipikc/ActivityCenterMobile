package com.kreipikc.activitycenter

import android.app.Application
import android.util.Log
import com.kreipikc.activitycenter.data.database.AppDatabase
import com.kreipikc.activitycenter.data.repository.AppUsageRepository
import com.kreipikc.activitycenter.utils.WorkScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class App : Application() {

    companion object {
        lateinit var database: AppDatabase
            private set
        lateinit var appUsageRepository: AppUsageRepository
            private set

        private const val APP_TAG = "app"
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getDatabase(this)
        appUsageRepository = AppUsageRepository(database)
        Log.d("'$APP_TAG'_db", "Init db success!")

        cleanupOldData()
        Log.d("'$APP_TAG'_db", "Cleared db success!")

        scheduleDailyWorker()
    }

    private fun cleanupOldData() {
        applicationScope.launch {
            kotlin.runCatching {
                appUsageRepository.cleanupOldData()
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

    private fun scheduleDailyWorker() {
        WorkScheduler.scheduleDailyStatsCollection(this) // everyday on 00:05

        // Для немедленного запуска раскомментируйте строку ниже (для Тестов)

        //WorkScheduler.scheduleNowForTesting(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}