package com.example.babyfood.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 同步拉取响应
 */
@Serializable
data class SyncPullResponse(
    val recipes: List<CloudRecipe>,
    val plans: List<CloudPlan>,
    val babies: List<CloudBaby>,
    val serverTime: Long // 服务器时间（毫秒时间戳）
)

/**
 * 同步推送请求
 */
@Serializable
data class SyncPushRequest(
    val recipes: List<CloudRecipe>,
    val plans: List<CloudPlan>,
    val babies: List<CloudBaby>
)

/**
 * 同步推送响应
 */
@Serializable
data class SyncPushResponse(
    val success: Boolean,
    val conflicts: List<ConflictInfo>,
    val serverTime: Long // 服务器时间（毫秒时间戳）
)

/**
 * 冲突信息
 */
@Serializable
data class ConflictInfo(
    val entityType: String,      // "recipe", "plan", "baby"
    val cloudId: String,          // 云端 ID
    val localVersion: Int,        // 本地版本
    val remoteVersion: Int,       // 远程版本
    val reason: String            // 冲突原因
)