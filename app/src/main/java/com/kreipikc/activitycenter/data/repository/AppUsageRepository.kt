package com.kreipikc.activitycenter.data.repository

import com.kreipikc.activitycenter.data.database.AppDatabase
import com.kreipikc.activitycenter.data.database.entity.AppEntity
import com.kreipikc.activitycenter.data.database.entity.DailyUsageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AppUsageRepository(private val database: AppDatabase) {

    private val appDao = database.appDao()
    private val dailyUsageDao = database.dailyUsageDao()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun getOrCreateApp(
        packageName: String,
        displayName: String
    ): AppEntity {
        return withContext(Dispatchers.IO) {
            val existingApp = appDao.getByPackageName(packageName)

            if (existingApp != null) {
                existingApp
            } else {
                val newApp = AppEntity(
                    packageName = packageName,
                    displayName = displayName
                )
                val id = appDao.insert(newApp)
                newApp.copy(id = id)
            }
        }
    }

    suspend fun saveUsage(
        appId: Long,
        usageSeconds: Long,
        date: String
    ) {
        withContext(Dispatchers.IO) {
            // Проверяем, есть ли уже запись на сегодня
            val existing = dailyUsageDao.getByDateAndApp(date, appId)

            if (existing != null) {
                // Обновляем существующую запись
                val updated = existing.copy(
                    usageSeconds = existing.usageSeconds + usageSeconds
                )
                dailyUsageDao.upsert(updated)
            } else {
                // Создаем новую запись
                val newUsage = DailyUsageEntity(
                    appId = appId,
                    date = date,
                    usageSeconds = usageSeconds
                )
                dailyUsageDao.upsert(newUsage)
            }
        }
    }

    suspend fun getUsageForDate(date: String): Flow<List<DailyUsageEntity>> {
        return withContext(Dispatchers.IO) {
            dailyUsageDao.getByDate(date)
        }
    }

    suspend fun getAppById(appId: Long): AppEntity? {
        return withContext(Dispatchers.IO) {
            appDao.getById(appId)
        }
    }

    suspend fun cleanupOldData() {
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -30) // Старше 30 дней
            val oldDate = dateFormatter.format(calendar.time)
            dailyUsageDao.deleteOlderThan(oldDate)
        }
    }

    private fun getTodayDate(): String {
        return dateFormatter.format(Date())
    }
}