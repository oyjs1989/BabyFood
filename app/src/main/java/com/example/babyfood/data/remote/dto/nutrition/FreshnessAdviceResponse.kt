package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.Serializable

/**
 * 新鲜度建议响应
 */
@Serializable
data class FreshnessAdviceResponse(
    val ingredientType: String,
    val storageMethod: String,  // REFRIGERATOR, FREEZER, ROOM_TEMPERATURE
    val shelfLifeDays: Int,
    val tips: String? = null
)
