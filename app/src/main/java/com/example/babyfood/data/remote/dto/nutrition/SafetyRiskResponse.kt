package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 安全风险响应
 */
@Serializable
data class SafetyRiskResponse(
    val id: Int,
    
    @SerialName("ingredientName")
    val ingredientName: String,
    
    @SerialName("riskLevel")
    val riskLevel: String,  // FORBIDDEN, NOT_RECOMMENDED, REQUIRES_SPECIAL_HANDLING, CAUTIOUS_INTRODUCTION, NORMAL
    
    @SerialName("minAgeMonths")
    val minAgeMonths: Int,
    
    @SerialName("maxAgeMonths")
    val maxAgeMonths: Int,
    
    @SerialName("warningMessage")
    val warningMessage: String? = null,
    
    @SerialName("handlingTips")
    val handlingTips: String? = null
)
