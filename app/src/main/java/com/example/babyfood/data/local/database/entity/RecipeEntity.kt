package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.babyfood.domain.model.Ingredient
import com.example.babyfood.domain.model.Nutrition
import com.example.babyfood.domain.model.Recipe

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val minAgeMonths: Int,
    val maxAgeMonths: Int,
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val nutrition: Nutrition,
    val category: String,
    val isBuiltIn: Boolean = false,
    val imageUrl: String? = null,

    // 同步元数据字段（向后兼容，默认值）
    val cloudId: String? = null,                    // 云端唯一标识
    val syncStatus: String = "PENDING_UPLOAD",      // 同步状态（默认待上传）
    val lastSyncTime: Long? = null,                 // 最后同步时间戳（毫秒）
    val version: Int = 1,                           // 版本号（用于冲突检测）
    val isDeleted: Boolean = false                  // 软删除标记
) {
    fun toDomainModel(): Recipe = Recipe(
        id = id,
        name = name,
        minAgeMonths = minAgeMonths,
        maxAgeMonths = maxAgeMonths,
        ingredients = ingredients,
        steps = steps,
        nutrition = nutrition,
        category = category,
        isBuiltIn = isBuiltIn,
        imageUrl = imageUrl
    )
}

fun Recipe.toEntity(): RecipeEntity = RecipeEntity(
    id = id,
    name = name,
    minAgeMonths = minAgeMonths,
    maxAgeMonths = maxAgeMonths,
    ingredients = ingredients,
    steps = steps,
    nutrition = nutrition,
    category = category,
    isBuiltIn = isBuiltIn,
    imageUrl = imageUrl
)