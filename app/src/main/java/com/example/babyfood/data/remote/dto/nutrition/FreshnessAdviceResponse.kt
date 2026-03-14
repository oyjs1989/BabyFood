package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 新鲜度与购买建议响应
 */
@Serializable
data class FreshnessAdviceResponse(
    @SerialName("ingredientName")
    val ingredientName: String,
    
    @SerialName("ageInMonths")
    val ageInMonths: Int,
    
    @SerialName("purchaseAdvice")
    val purchaseAdvice: String, // HOMEMADE, STORE_BOUGHT, BOTH
    
    val reason: String,
    
    @SerialName("storageWarning")
    val storageWarning: String? = null
)
