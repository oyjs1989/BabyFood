package com.example.babyfood.data.remote.dto.ingredient_trials

import kotlinx.serialization.Serializable

/**
 * 创建食材尝试记录请求 DTO
 */
@Serializable
data class IngredientTrialCreate(
    val babyId: Int,
    val ingredientName: String,
    val trialDate: String,
    val reaction: String = "NONE",  // NONE, MILD, MODERATE, SEVERE
    val notes: String? = null
)
