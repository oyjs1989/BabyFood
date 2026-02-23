package com.example.babyfood.data.remote.dto.ingredient_trials

import kotlinx.serialization.Serializable

/**
 * 食材尝试记录响应 DTO
 */
@Serializable
data class IngredientTrialResponse(
    val id: Int,
    val babyId: Int,
    val ingredientName: String,
    val trialDate: String,
    val reaction: String,  // NONE, MILD, MODERATE, SEVERE
    val notes: String? = null,
    val createdAt: String
)
