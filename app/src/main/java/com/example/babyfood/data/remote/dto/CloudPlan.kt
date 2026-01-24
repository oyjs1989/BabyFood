package com.example.babyfood.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 云端餐单计划数据模型（脱敏版本）
 * 不包含敏感信息：babyId（使用 cloudBabyId 替代）
 */
@Serializable
data class CloudPlan(
    val cloudId: String,                        // 云端唯一标识
    val cloudBabyId: String?,                   // 云端宝宝 ID（可为空，用于云端关联）
    val cloudRecipeId: String,                  // 云端食谱 ID
    val plannedDate: String,                    // 计划日期（ISO-8601 格式）
    val mealPeriod: String,                     // 餐段时间（BREAKFAST、LUNCH、DINNER、SNACK）
    val status: String,                         // 状态（PLANNED、TRIED、SKIPPED）
    val notes: String?,                         // 备注
    val createdAt: Long,                        // 创建时间（毫秒时间戳）
    val updatedAt: Long,                        // 更新时间（毫秒时间戳）
    val version: Int                            // 版本号（用于冲突检测）
)