package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 营养目标响应
 */
@Serializable
data class NutritionGoalsResponse(
    @SerialName("babyId")
    val babyId: Int,
    
    val calories: Double? = null,
    val protein: Double? = null,
    val calcium: Double? = null,
    val iron: Double? = null,
    
    @SerialName("vitaminA")
    val vitaminA: Double? = null,
    
    @SerialName("vitaminC")
    val vitaminC: Double? = null,
    
    @SerialName("updatedAt")
    val updatedAt: String
)

/**
 * 营养目标更新请求
 */
@Serializable
data class NutritionGoalsUpdate(
    val calories: Double? = null,
    val protein: Double? = null,
    val calcium: Double? = null,
    val iron: Double? = null,
    
    @SerialName("vitaminA")
    val vitaminA: Double? = null,
    
    @SerialName("vitaminC")
    val vitaminC: Double? = null
)
