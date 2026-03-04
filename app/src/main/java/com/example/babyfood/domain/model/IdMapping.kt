package com.example.babyfood.domain.model

import kotlinx.datetime.Instant

/**
 * ID 映射领域模型
 * 记录云端 ID 与本地 ID 的对应关系
 */
data class IdMapping(
    val id: Long = 0,
    val entityType: EntityType,
    val localId: Long,
    val cloudId: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
