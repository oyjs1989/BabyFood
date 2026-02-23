package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 营养目标响应
 */
@Serializable
data class NutritionGoalsResponse(
    val id: Int,
    
    @SerialName("babyId")
    val babyId: Int,
    
    val calories: Double,
    val protein: Double,
    val calcium: Double,
    val iron: Double,
    
    @SerialName("createdAt")
    val createdAt: String,
    
    @SerialName("updatedAt")
    val updatedAt: String
)

/**
 * 营养目标更新请求
 */
@Serializable
data class NutritionGoalsUpdate(
    val calories: Double,
    val protein: Double,
    val calcium: Double,
    val iron: Double
)
