package com.example.babyfood.data.remote.dto

import com.example.babyfood.data.remote.dto.serializers.LocalDateSerializer
import com.example.babyfood.domain.model.AllergyItem
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.PreferenceItem
import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * 云端宝宝数据模型（脱敏版本）
 * 不包含敏感信息：name、birthDate、allergies、weight、height
 */
@Serializable
data class CloudBaby(
    val cloudId: String,                        // 云端唯一标识
    val gender: String? = null,                 // 性别（新增）
    val preferences: List<CloudPreferenceItem> = emptyList(), // 偏好食材（非敏感）
    val nutritionGoal: CloudNutritionGoal? = null,     // 营养目标（非敏感）
    val avatarUrl: String? = null,              // 头像URL（新增）
    val createdAt: Long,                        // 创建时间（毫秒时间戳）
    val updatedAt: Long,                        // 更新时间（毫秒时间戳）
    val version: Int                            // 版本号（用于冲突检测）
)

/**
 * 云端食材数据模型
 */
@Serializable
data class CloudIngredient(
    val name: String,
    val amount: String?,
    val unit: String?
)

/**
 * 云端营养信息数据模型
 */
@Serializable
data class CloudNutrition(
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbohydrates: Float,
    val fiber: Float?,
    val calcium: Float?,
    val iron: Float?,
    val zinc: Float?
)

/**
 * 云端偏好食材数据模型
 */
@Serializable
data class CloudPreferenceItem(
    val ingredient: String,
    val expiryDate: String?
)

/**
 * 云端营养目标数据模型
 */
@Serializable
data class CloudNutritionGoal(
    val calories: Float,
    val protein: Float,
    val calcium: Float,
    val iron: Float
)