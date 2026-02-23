package com.example.babyfood.data.remote.mapper

import com.example.babyfood.data.local.database.entity.RecipeEntity
import com.example.babyfood.data.remote.dto.CloudIngredient
import com.example.babyfood.data.remote.dto.CloudNutrition
import com.example.babyfood.data.remote.dto.CloudRecipe
import com.example.babyfood.domain.model.Ingredient
import com.example.babyfood.domain.model.Nutrition
import kotlinx.datetime.Clock

/**
 * 食谱数据映射器
 * 用于 Entity ↔ Cloud 之间的转换
 */
object RecipeMapper {

    /**
     * 将 Entity 转换为 Cloud 模型
     */
    fun toCloud(entity: RecipeEntity): CloudRecipe {
        return CloudRecipe(
            cloudId = entity.cloudId ?: generateCloudId(),
            name = entity.name,
            minAgeMonths = entity.minAgeMonths,
            maxAgeMonths = entity.maxAgeMonths,
            ingredients = entity.ingredients.map { toCloudIngredient(it) },
            steps = entity.steps,
            nutrition = toCloudNutrition(entity.nutrition),
            category = entity.category,
            isBuiltIn = entity.isBuiltIn,
            imageUrl = entity.imageUrl,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            version = entity.version
        )
    }

    /**
     * 将 Cloud 模型转换为 Entity
     */
    fun toEntity(cloud: CloudRecipe): RecipeEntity {
        return RecipeEntity(
            id = 0L, // 由数据库自动生成
            name = cloud.name,
            minAgeMonths = cloud.minAgeMonths,
            maxAgeMonths = cloud.maxAgeMonths,
            ingredients = cloud.ingredients.map { toIngredient(it) },
            steps = cloud.steps,
            nutrition = toNutrition(cloud.nutrition),
            category = cloud.category,
            isBuiltIn = cloud.isBuiltIn,
            imageUrl = cloud.imageUrl,
            cloudId = cloud.cloudId,
            syncStatus = "SYNCED",
            lastSyncTime = Clock.System.now().toEpochMilliseconds(),
            version = cloud.version
        )
    }

    /**
     * 将 Ingredient 转换为 CloudIngredient
     */
    private fun toCloudIngredient(ingredient: com.example.babyfood.domain.model.Ingredient): com.example.babyfood.data.remote.dto.CloudIngredient {
        return com.example.babyfood.data.remote.dto.CloudIngredient(
            name = ingredient.name,
            amount = ingredient.amount,
            unit = null  // Ingredient 模型中没有 unit 字段
        )
    }

    /**
     * 将 CloudIngredient 转换为 Ingredient
     */
    private fun toIngredient(ingredient: com.example.babyfood.data.remote.dto.CloudIngredient): com.example.babyfood.domain.model.Ingredient {
        return com.example.babyfood.domain.model.Ingredient(
            name = ingredient.name,
            amount = ingredient.amount ?: "",
            isAllergen = false
        )
    }

    /**
     * 将 Nutrition 转换为 CloudNutrition
     */
    private fun toCloudNutrition(nutrition: com.example.babyfood.domain.model.Nutrition): com.example.babyfood.data.remote.dto.CloudNutrition {
        return com.example.babyfood.data.remote.dto.CloudNutrition(
            calories = nutrition.calories ?: 0f,
            protein = nutrition.protein ?: 0f,
            fat = nutrition.fat ?: 0f,
            carbohydrates = nutrition.carbohydrates ?: 0f,
            fiber = nutrition.fiber,
            calcium = nutrition.calcium,
            iron = nutrition.iron,
            zinc = null  // Nutrition 模型中没有 zinc 字段
        )
    }

    /**
     * 将 CloudNutrition 转换为 Nutrition
     */
    private fun toNutrition(nutrition: com.example.babyfood.data.remote.dto.CloudNutrition): com.example.babyfood.domain.model.Nutrition {
        return com.example.babyfood.domain.model.Nutrition(
            calories = nutrition.calories,
            protein = nutrition.protein,
            fat = nutrition.fat,
            carbohydrates = nutrition.carbohydrates,
            fiber = nutrition.fiber,
            calcium = nutrition.calcium,
            iron = nutrition.iron
        )
    }

    /**
     * 生成云端 ID
     */
    private fun generateCloudId(): String {
        return "recipe_${System.currentTimeMillis()}_${(0..9999).random()}"
    }
}