package com.example.babyfood.data.remote.mapper

import com.example.babyfood.data.local.database.entity.BabyEntity
import com.example.babyfood.data.remote.dto.CloudBaby
import com.example.babyfood.data.remote.dto.CloudNutritionGoal
import com.example.babyfood.data.remote.dto.CloudPreferenceItem
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.PreferenceItem
import kotlinx.datetime.Clock

/**
 * 宝宝数据映射器
 * 用于 Entity ↔ Cloud 之间的转换
 */
object BabyMapper {

    /**
     * 将 Entity 转换为 Cloud 模型（脱敏）
     * 不包含敏感信息：name、birthDate、allergies、weight、height
     */
    fun toCloud(entity: BabyEntity): CloudBaby {
        return CloudBaby(
            cloudId = entity.cloudId ?: generateCloudId(),
            preferences = entity.preferences.map { toCloudPreferenceItem(it) },
            nutritionGoal = entity.nutritionGoal?.let { toCloudNutritionGoal(it) },
            createdAt = Clock.System.now().toEpochMilliseconds(),
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            version = entity.version
        )
    }

    /**
     * 将 Cloud 模型转换为 Entity（合并敏感信息）
     * 需要从本地 Entity 中获取敏感信息
     */
    fun toEntity(cloud: CloudBaby, localEntity: BabyEntity): BabyEntity {
        return localEntity.copy(
            cloudId = cloud.cloudId,
            preferences = cloud.preferences.map { toPreferenceItem(it) },
            nutritionGoal = cloud.nutritionGoal?.let { toNutritionGoal(it) },
            syncStatus = "SYNCED",
            lastSyncTime = Clock.System.now().toEpochMilliseconds(),
            version = cloud.version
        )
    }

    /**
     * 将 PreferenceItem 转换为 CloudPreferenceItem
     */
    private fun toCloudPreferenceItem(item: com.example.babyfood.domain.model.PreferenceItem): CloudPreferenceItem {
        return CloudPreferenceItem(
            ingredient = item.ingredient,
            expiryDate = item.expiryDate
        )
    }

    /**
     * 将 CloudPreferenceItem 转换为 PreferenceItem
     */
    private fun toPreferenceItem(item: CloudPreferenceItem): com.example.babyfood.domain.model.PreferenceItem {
        return com.example.babyfood.domain.model.PreferenceItem(
            ingredient = item.ingredient,
            expiryDate = item.expiryDate
        )
    }

    /**
     * 将 NutritionGoal 转换为 CloudNutritionGoal
     */
    private fun toCloudNutritionGoal(goal: com.example.babyfood.domain.model.NutritionGoal): CloudNutritionGoal {
        return CloudNutritionGoal(
            calories = goal.calories,
            protein = goal.protein,
            calcium = goal.calcium,
            iron = goal.iron
        )
    }

    /**
     * 将 CloudNutritionGoal 转换为 NutritionGoal
     */
    private fun toNutritionGoal(goal: CloudNutritionGoal): com.example.babyfood.domain.model.NutritionGoal {
        return com.example.babyfood.domain.model.NutritionGoal(
            calories = goal.calories,
            protein = goal.protein,
            calcium = goal.calcium,
            iron = goal.iron
        )
    }

    /**
     * 生成云端 ID
     * TODO: 使用 UUID 或其他唯一 ID 生成策略
     */
    private fun generateCloudId(): String {
        return "baby_${System.currentTimeMillis()}_${(0..9999).random()}"
    }
}