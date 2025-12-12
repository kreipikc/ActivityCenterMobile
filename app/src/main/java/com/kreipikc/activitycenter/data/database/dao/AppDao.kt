package com.kreipikc.activitycenter.data.database.dao

import androidx.room.*
import com.kreipikc.activitycenter.data.database.entity.AppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(app: AppEntity): Long

    @Update
    suspend fun update(app: AppEntity)

    @Delete
    suspend fun delete(app: AppEntity)

    @Query("SELECT * FROM apps WHERE package_name = :packageName LIMIT 1")
    suspend fun getByPackageName(packageName: String): AppEntity?

    @Query("SELECT * FROM apps WHERE id = :id")
    suspend fun getById(id: Long): AppEntity?

    @Query("SELECT * FROM apps")
    fun getAll(): Flow<List<AppEntity>>

    @Query("SELECT * FROM apps WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<AppEntity>

    @Query("DELETE FROM apps")
    suspend fun deleteAll()
}