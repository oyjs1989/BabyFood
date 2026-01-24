package com.example.babyfood.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 云端食谱数据模型
 * 不包含敏感信息，完全可公开存储
 */
@Serializable
data class CloudRecipe(
    val cloudId: String,                        // 云端唯一标识
    val name: String,                           // 食谱名称
    val minAgeMonths: Int,                      // 最小月龄
    val maxAgeMonths: Int,                      // 最大月龄
    val ingredients: List<CloudIngredient>,     // 食材列表
    val steps: List<String>,                    // 制作步骤
    val nutrition: CloudNutrition,              // 营养信息
    val category: String,                       // 分类
    val isBuiltIn: Boolean,                     // 是否内置食谱
    val imageUrl: String?,                      // 图片 URL
    val createdAt: Long,                        // 创建时间（毫秒时间戳）
    val updatedAt: Long,                        // 更新时间（毫秒时间戳）
    val version: Int                            // 版本号（用于冲突检测）
)