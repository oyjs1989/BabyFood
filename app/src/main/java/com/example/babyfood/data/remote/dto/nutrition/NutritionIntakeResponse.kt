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
    val actualIntake: Map<String, Double>,
    
    val goals: NutritionGoalsResponse,
    
    @SerialName("matchPercentage")
    val matchPercentage: Map<String, Double>,
    
    val warnings: List<String> = emptyList()
)
