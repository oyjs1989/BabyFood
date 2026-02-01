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
    val cookingTime: Int? = null,   // 烹饪时间（分钟）
    val isBuiltIn: Boolean = false,
    val imageUrl: String? = null,

    // 营养功能扩展字段
    val textureType: String? = null,           // 质地类型：PUREE, MASH, CHUNK, SOLID
    val isIronRich: Boolean = false,            // 是否富铁食谱
    val ironContent: Double? = null,            // 铁含量（mg/100g）
    val riskLevelList: String? = null,          // 风险等级列表（JSON格式）
    val safetyAdvice: String? = null,           // 安全建议

    // 同步元数据字段（向后兼容，默认值）
    override val cloudId: String? = null,           // 云端唯一标识
    override val syncStatus: String = "PENDING_UPLOAD", // 同步状态（默认待上传）
    override val lastSyncTime: Long? = null,        // 最后同步时间戳（毫秒）
    override val version: Int = 1,                  // 版本号（用于冲突检测）
    override val isDeleted: Boolean = false         // 软删除标记
) : SyncableEntity {
    fun toDomainModel(): Recipe = Recipe(
        id = id,
        name = name,
        minAgeMonths = minAgeMonths,
        maxAgeMonths = maxAgeMonths,
        ingredients = ingredients,
        steps = steps,
        nutrition = nutrition,
        category = category,
        cookingTime = cookingTime,
        isBuiltIn = isBuiltIn,
        imageUrl = imageUrl,
        textureType = textureType,
        isIronRich = isIronRich,
        ironContent = ironContent,
        riskLevelList = riskLevelList,
        safetyAdvice = safetyAdvice
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
    cookingTime = cookingTime,
    isBuiltIn = isBuiltIn,
    imageUrl = imageUrl,
    textureType = textureType,
    isIronRich = isIronRich,
    ironContent = ironContent,
    riskLevelList = riskLevelList,
    safetyAdvice = safetyAdvice
)