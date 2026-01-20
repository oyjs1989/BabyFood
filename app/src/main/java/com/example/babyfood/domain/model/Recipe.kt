package com.example.babyfood.domain.model

data class Recipe(
    val id: Long = 0,
    val name: String,
    val minAgeMonths: Int,   // 最小月龄
    val maxAgeMonths: Int,   // 最大月龄
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val nutrition: Nutrition,
    val category: String,
    val isBuiltIn: Boolean = false,   // 是否内置食谱
    val imageUrl: String? = null
)

data class Ingredient(
    val name: String,
    val amount: String,  // 例如：100g
    val isAllergen: Boolean = false
)

data class Nutrition(
    val calories: Float?,    // kcal
    val protein: Float?,     // g
    val fat: Float?,         // g
    val carbohydrates: Float?, // g
    val fiber: Float?,       // g
    val calcium: Float?,     // mg
    val iron: Float?         // mg
)