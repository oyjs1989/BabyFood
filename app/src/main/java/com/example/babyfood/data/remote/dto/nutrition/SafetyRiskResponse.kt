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
    
    @SerialName("riskReason")
    val riskReason: String,
    
    @SerialName("handlingAdvice")
    val handlingAdvice: String? = null,
    
    @SerialName("applicableAgeRangeStart")
    val applicableAgeRangeStart: Int? = null,
    
    @SerialName("applicableAgeRangeEnd")
    val applicableAgeRangeEnd: Int? = null,
    
    val severity: Int,
    
    @SerialName("dataSource")
    val dataSource: String
)
