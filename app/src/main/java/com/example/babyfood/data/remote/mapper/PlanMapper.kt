package com.example.babyfood.data.remote.mapper

import com.example.babyfood.data.local.database.entity.PlanEntity
import com.example.babyfood.data.remote.dto.CloudPlan
import kotlinx.datetime.Clock

/**
 * 餐单计划数据映射器
 * 用于 Entity ↔ Cloud 之间的转换
 */
object PlanMapper {

    /**
     * 将 Entity 转换为 Cloud 模型
     */
    fun toCloud(entity: PlanEntity): CloudPlan {
        return CloudPlan(
            cloudId = entity.cloudId ?: generateCloudId(),
            cloudBabyId = entity.cloudBabyId,
            cloudRecipeId = entity.cloudRecipeId ?: generateCloudRecipeId(entity.recipeId),
            plannedDate = entity.plannedDate.toString(),
            mealPeriod = entity.mealPeriod.name,
            status = entity.status.name,
            notes = entity.notes,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            version = entity.version
        )
    }

    /**
     * 将 Cloud 模型转换为 Entity
     */
    fun toEntity(cloud: CloudPlan): PlanEntity {
        return PlanEntity(
            id = 0L, // 由数据库自动生成
            babyId = 0L, // 需要从本地映射表获取
            recipeId = 0L, // 需要从本地映射表获取
            plannedDate = kotlinx.datetime.LocalDate.parse(cloud.plannedDate),
            mealPeriod = com.example.babyfood.domain.model.MealPeriod.valueOf(cloud.mealPeriod),
            status = com.example.babyfood.domain.model.PlanStatus.valueOf(cloud.status),
            notes = cloud.notes,
            cloudId = cloud.cloudId,
            cloudBabyId = cloud.cloudBabyId,
            cloudRecipeId = cloud.cloudRecipeId,
            syncStatus = "SYNCED",
            lastSyncTime = Clock.System.now().toEpochMilliseconds(),
            version = cloud.version
        )
    }

    /**
     * 生成云端 ID
     */
    private fun generateCloudId(): String {
        return "plan_${System.currentTimeMillis()}_${(0..9999).random()}"
    }

    /**
     * 生成云端食谱 ID
     * TODO: 从本地映射表获取真实的 cloudRecipeId
     */
    private fun generateCloudRecipeId(recipeId: Long): String {
        return "recipe_$recipeId"
    }
}