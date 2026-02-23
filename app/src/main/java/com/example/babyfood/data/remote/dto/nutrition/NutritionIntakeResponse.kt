package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 营养摄入响应
 */
@Serializable
data class NutritionIntakeResponse(
    @SerialName("babyId")
    val babyId: Int,
    
    @SerialName("actualIntake")
    val actualIntake: NutritionIntakeData,
    
    val goals: NutritionGoalsResponse,
    
    val warnings: List<String> = emptyList()
)

/**
 * 营养摄入数据
 */
@Serializable
data class NutritionIntakeData(
    val calories: Double,
    val protein: Double,
    val calcium: Double,
    val iron: Double
)
