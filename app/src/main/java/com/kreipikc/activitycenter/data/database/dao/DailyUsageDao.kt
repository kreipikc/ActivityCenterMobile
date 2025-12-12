package com.kreipikc.activitycenter.data.database.dao

import androidx.room.*
import com.kreipikc.activitycenter.data.database.entity.DailyUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyUsageDao {
    @Upsert
    suspend fun upsert(usage: DailyUsageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usages: List<DailyUsageEntity>)

    @Update
    suspend fun update(usage: DailyUsageEntity)

    @Delete
    suspend fun delete(usage: DailyUsageEntity)

    @Query("SELECT * FROM daily_usage WHERE date = :date AND app_id = :appId")
    suspend fun getByDateAndApp(date: String, appId: Long): DailyUsageEntity?

    @Query("SELECT * FROM daily_usage WHERE date = :date")
    fun getByDate(date: String): Flow<List<DailyUsageEntity>>

    @Query("SELECT * FROM daily_usage WHERE date BETWEEN :startDate AND :endDate AND app_id = :appId")
    fun getByDateRangeAndApp(
        startDate: String,
        endDate: String,
        appId: Long
    ): Flow<List<DailyUsageEntity>>

    @Query("SELECT SUM(usage_seconds) FROM daily_usage WHERE date = :date")
    suspend fun getTotalUsageForDate(date: String): Long?

    @Query("SELECT SUM(usage_seconds) FROM daily_usage WHERE app_id = :appId AND date = :date")
    suspend fun getUsageForAppAndDate(appId: Long, date: String): Long?

    @Query("DELETE FROM daily_usage WHERE date < :oldDate")
    suspend fun deleteOlderThan(oldDate: String)

    @Query("DELETE FROM daily_usage WHERE date = :date")
    suspend fun deleteByDate(date: String)

    @Query("DELETE FROM daily_usage")
    suspend fun deleteAll()
}