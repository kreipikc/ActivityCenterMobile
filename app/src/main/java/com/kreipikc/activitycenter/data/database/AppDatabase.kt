package com.kreipikc.activitycenter.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kreipikc.activitycenter.data.database.dao.AppDao
import com.kreipikc.activitycenter.data.database.dao.DailyUsageDao
import com.kreipikc.activitycenter.data.database.entity.AppEntity
import com.kreipikc.activitycenter.data.database.entity.DailyUsageEntity

@Database(
    entities = [AppEntity::class, DailyUsageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun dailyUsageDao(): DailyUsageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_usage.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}