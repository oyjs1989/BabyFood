package com.example.babyfood.data.repository

import android.util.Log
import com.example.babyfood.data.local.database.dao.IdMappingDao
import com.example.babyfood.data.local.database.entity.toDomainModel
import com.example.babyfood.data.local.database.entity.toEntity
import com.example.babyfood.domain.model.EntityType
import com.example.babyfood.domain.model.IdMapping
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ID 映射仓库
 * 管理云端 ID 与本地 ID 的映射关系
 */
@Singleton
class IdMappingRepository @Inject constructor(
    private val idMappingDao: IdMappingDao
) {
    companion object {
        private const val TAG = "IdMappingRepository"
    }

    /**
     * 根据本地 ID 获取映射
     */
    suspend fun getByLocalId(entityType: EntityType, localId: Long): IdMapping? {
        Log.d(TAG, "========== getByLocalId 开始 ==========")
        Log.d(TAG, "entityType: $entityType, localId: $localId")
        
        val entity = idMappingDao.getByLocalId(entityType.name, localId)
        val result = entity?.toDomainModel()
        
        Log.d(TAG, "查询结果: ${result?.let { "找到映射 cloudId=${it.cloudId}" } ?: "未找到映射"}")
        Log.d(TAG, "========== getByLocalId 结束 ==========")
        
        return result
    }

    /**
     * 根据云端 ID 获取映射
     */
    suspend fun getByCloudId(entityType: EntityType, cloudId: String): IdMapping? {
        Log.d(TAG, "========== getByCloudId 开始 ==========")
        Log.d(TAG, "entityType: $entityType, cloudId: $cloudId")
        
        val entity = idMappingDao.getByCloudId(entityType.name, cloudId)
        val result = entity?.toDomainModel()
        
        Log.d(TAG, "查询结果: ${result?.let { "找到映射 localId=${it.localId}" } ?: "未找到映射"}")
        Log.d(TAG, "========== getByCloudId 结束 ==========")
        
        return result
    }

    /**
     * 获取指定实体类型的所有映射
     */
    fun getByEntityType(entityType: EntityType): Flow<List<IdMapping>> {
        Log.d(TAG, "获取 $entityType 类型的所有映射 Flow")
        return idMappingDao.getByEntityType(entityType.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * 获取所有映射
     */
    fun getAll(): Flow<List<IdMapping>> {
        Log.d(TAG, "获取所有映射 Flow")
        return idMappingDao.getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * 保存映射（插入或更新）
     */
    suspend fun save(mapping: IdMapping): Long {
        Log.d(TAG, "========== save 开始 ==========")
        Log.d(TAG, "保存映射: entityType=${mapping.entityType}, localId=${mapping.localId}, cloudId=${mapping.cloudId}")
        
        val id = idMappingDao.insert(mapping.toEntity())
        
        Log.d(TAG, "✓ 映射保存成功, id=$id")
        Log.d(TAG, "========== save 结束 ==========")
        
        return id
    }

    /**
     * 更新或插入映射
     * 如果存在则更新，不存在则插入
     */
    suspend fun upsert(entityType: EntityType, localId: Long, cloudId: String) {
        Log.d(TAG, "========== upsert 开始 ==========")
        Log.d(TAG, "entityType: $entityType, localId: $localId, cloudId: $cloudId")
        
        idMappingDao.upsert(entityType.name, localId, cloudId)
        
        Log.d(TAG, "✓ upsert 完成")
        Log.d(TAG, "========== upsert 结束 ==========")
    }

    /**
     * 删除映射
     */
    suspend fun delete(mapping: IdMapping) {
        Log.d(TAG, "删除映射: entityType=${mapping.entityType}, localId=${mapping.localId}")
        idMappingDao.delete(mapping.toEntity())
    }

    /**
     * 根据本地 ID 删除映射
     */
    suspend fun deleteByLocalId(entityType: EntityType, localId: Long) {
        Log.d(TAG, "删除映射: entityType=$entityType, localId=$localId")
        idMappingDao.deleteByLocalId(entityType.name, localId)
    }

    /**
     * 根据云端 ID 获取本地 ID
     * 如果不存在映射则返回 null
     */
    suspend fun getLocalId(entityType: EntityType, cloudId: String): Long? {
        return getByCloudId(entityType, cloudId)?.localId
    }

    /**
     * 根据本地 ID 获取云端 ID
     * 如果不存在映射则返回 null
     */
    suspend fun getCloudId(entityType: EntityType, localId: Long): String? {
        return getByLocalId(entityType, localId)?.cloudId
    }
}
