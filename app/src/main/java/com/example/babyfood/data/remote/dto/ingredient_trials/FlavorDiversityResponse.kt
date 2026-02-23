package com.example.babyfood.data.remote.dto.ingredient_trials

import kotlinx.serialization.Serializable

/**
 * 口味多样性响应 DTO
 */
@Serializable
data class FlavorDiversityResponse(
    val babyId: Int,
    val totalTried: Int,
    val diversityScore: Double,  // 0-100
    val newIngredientsRecommendations: List<String>,
    val triedCategories: List<String>
)
