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
    val imageUrl: String? = null,               // 图片 URL
    // Phase 2: 新增字段
    val cookingTime: Int? = null,               // 烹饪时间（分钟）
    val difficulty: String? = null,             // 难度等级
    val description: String? = null,            // 食谱描述
    val tags: List<String>? = null,             // 标签列表
    // 营养指导字段
    val textureType: String? = null,            // 质地类型
    val isIronRich: Boolean? = null,            // 是否高铁食谱
    val ironContent: Float? = null,             // 铁含量（mg）
    val riskLevelList: List<String>? = null,    // 风险等级列表
    val safetyAdvice: String? = null,           // 安全建议
    val createdAt: Long,                        // 创建时间（毫秒时间戳）
    val updatedAt: Long,                        // 更新时间（毫秒时间戳）
    val version: Int                            // 版本号（用于冲突检测）
)