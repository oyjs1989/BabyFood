package com.example.babyfood.domain.model

import kotlinx.datetime.Instant

/**
 * 冲突信息
 * 记录同步过程中发现的冲突
 */
data class ConflictInfo(
    val entityType: EntityType,
    val localId: Long,
    val cloudId: String,
    val localVersion: Int,
    val cloudVersion: Int,
    val conflictReason: String,
    val resolvedAt: Instant? = null,
    val resolution: ConflictResolution? = null
)

/**
 * 冲突解决策略
 */
enum class ConflictResolution {
    /**
     * 本地版本优先
     */
    LOCAL_WINS,

    /**
     * 云端版本优先
     */
    CLOUD_WINS,

    /**
     * 手动解决
     */
    MANUAL
}
