package com.example.babyfood.domain.model

import kotlin.time.Duration

/**
 * 同步结果
 * 记录一次同步操作的结果
 */
data class SyncResult(
    val success: Boolean,
    val pulledCount: Int = 0,
    val pushedCount: Int = 0,
    val conflictCount: Int = 0,
    val errors: List<SyncError> = emptyList(),
    val duration: Duration
)

/**
 * 同步错误
 */
data class SyncError(
    val entityType: EntityType?,
    val entityId: String?,
    val message: String,
    val throwable: Throwable? = null
)
