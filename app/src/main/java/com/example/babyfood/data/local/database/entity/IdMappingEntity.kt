package com.example.babyfood.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.EntityType
import com.example.babyfood.domain.model.IdMapping
import kotlinx.datetime.Instant

/**
 * ID 映射实体
 * 存储云端 ID 与本地 ID 的映射关系
 */
@Entity(
    tableName = "id_mappings",
    indices = [
        Index(value = ["entity_type", "local_id"], unique = true),
        Index(value = ["entity_type", "cloud_id"], unique = true)
    ]
)
data class IdMappingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "entity_type")
    val entityType: String,
    @ColumnInfo(name = "local_id")
    val localId: Long,
    @ColumnInfo(name = "cloud_id")
    val cloudId: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Entity 转 Domain Model
 */
fun IdMappingEntity.toDomainModel(): IdMapping = IdMapping(
    id = id,
    entityType = EntityType.valueOf(entityType),
    localId = localId,
    cloudId = cloudId,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt)
)

/**
 * Domain Model 转 Entity
 */
fun IdMapping.toEntity(): IdMappingEntity = IdMappingEntity(
    id = id,
    entityType = entityType.name,
    localId = localId,
    cloudId = cloudId,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt.toEpochMilliseconds()
)