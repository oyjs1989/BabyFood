package com.example.babyfood.data.remote.dto.ingredient_trials

import kotlinx.serialization.Serializable

/**
 * 更新食材尝试记录请求 DTO
 */
@Serializable
data class IngredientTrialUpdate(
    val reaction: String? = null,  // NONE, MILD, MODERATE, SEVERE
    val notes: String? = null
)
