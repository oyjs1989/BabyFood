package com.example.babyfood.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.babyfood.data.local.database.entity.IdMappingEntity
import kotlinx.coroutines.flow.Flow

/**
 * ID 映射 DAO
 * 管理云端 ID 与本地 ID 的映射关系
 */
@Dao
interface IdMappingDao {

    /**
     * 根据本地 ID 查询映射
     */
    @Query("SELECT * FROM id_mappings WHERE entity_type = :entityType AND local_id = :localId")
    suspend fun getByLocalId(entityType: String, localId: Long): IdMappingEntity?

    /**
     * 根据云端 ID 查询映射
     */
    @Query("SELECT * FROM id_mappings WHERE entity_type = :entityType AND cloud_id = :cloudId")
    suspend fun getByCloudId(entityType: String, cloudId: String): IdMappingEntity?

    /**
     * 获取指定实体类型的所有映射
     */
    @Query("SELECT * FROM id_mappings WHERE entity_type = :entityType")
    fun getByEntityType(entityType: String): Flow<List<IdMappingEntity>>

    /**
     * 获取所有映射
     */
    @Query("SELECT * FROM id_mappings")
    fun getAll(): Flow<List<IdMappingEntity>>

    /**
     * 插入映射
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mapping: IdMappingEntity): Long

    /**
     * 更新映射
     */
    @Update
    suspend fun update(mapping: IdMappingEntity)

    /**
     * 删除映射
     */
    @Delete
    suspend fun delete(mapping: IdMappingEntity)

    /**
     * 根据本地 ID 删除映射
     */
    @Query("DELETE FROM id_mappings WHERE entity_type = :entityType AND local_id = :localId")
    suspend fun deleteByLocalId(entityType: String, localId: Long)

    /**
     * 根据云端 ID 删除映射
     */
    @Query("DELETE FROM id_mappings WHERE entity_type = :entityType AND cloud_id = :cloudId")
    suspend fun deleteByCloudId(entityType: String, cloudId: String)

    /**
     * 清空所有映射
     */
    @Query("DELETE FROM id_mappings")
    suspend fun deleteAll()

    /**
     * 更新或插入映射
     * 如果存在则更新，不存在则插入
     */
    @Transaction
    suspend fun upsert(entityType: String, localId: Long, cloudId: String) {
        val existing = getByLocalId(entityType, localId)
        if (existing != null) {
            update(existing.copy(cloudId = cloudId, updatedAt = System.currentTimeMillis()))
        } else {
            insert(
                IdMappingEntity(
                    entityType = entityType,
                    localId = localId,
                    cloudId = cloudId
                )
            )
        }
    }
}
