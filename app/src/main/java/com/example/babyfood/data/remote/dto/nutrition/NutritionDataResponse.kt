package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 营养数据响应
 */
@Serializable
data class NutritionDataResponse(
    val id: Int,
    
    @SerialName("ingredientName")
    val ingredientName: String,
    
    val calories: Double? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbohydrates: Double? = null,
    val fiber: Double? = null,
    val calcium: Double? = null,
    val iron: Double? = null,
    val zinc: Double? = null,
    
    @SerialName("vitaminA")
    val vitaminA: Double? = null,
    
    @SerialName("vitaminC")
    val vitaminC: Double? = null
)
