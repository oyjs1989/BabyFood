package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

data class Recipe(
    val id: Long = 0,
    val name: String,
    val minAgeMonths: Int,   // 最小月龄
    val maxAgeMonths: Int,   // 最大月龄
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val nutrition: Nutrition,
    val category: String,
    val cookingTime: Int? = null,   // 烹饪时间（分钟）
    val isBuiltIn: Boolean = false,   // 是否内置食谱
    val imageUrl: String? = null,
    val textureType: String? = null,           // 质地类型：PUREE, MASH, CHUNK, SOLID
    val isIronRich: Boolean = false,            // 是否富铁食谱
    val ironContent: Double? = null,            // 铁含量（mg/100g）
    val riskLevelList: String? = null,          // 风险等级列表（JSON格式）
    val safetyAdvice: String? = null            // 安全建议
)

@Serializable
data class Ingredient(
    val name: String,
    val amount: String,  // 例如：100g
    val isAllergen: Boolean = false
)

@Serializable
data class Nutrition(
    val calories: Float?,    // kcal
    val protein: Float?,     // g
    val fat: Float?,         // g
    val carbohydrates: Float?, // g
    val fiber: Float?,       // g
    val calcium: Float?,     // mg
    val iron: Float?         // mg
)