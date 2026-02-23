package com.example.babyfood.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 健康分析请求 DTO
 */
@Serializable
data class HealthAnalysisRequest(
    val weight: Float?,
    val height: Float?,
    val headCircumference: Float?,
    val ironLevel: Float?,
    val calciumLevel: Float?,
    val hemoglobin: Float?,
    val recordDate: String,
    val notes: String?
)

/**
 * 健康分析响应 DTO
 */
@Serializable
data class HealthAnalysisResponse(
    val success: Boolean,
    val conclusion: String?,
    val recommendations: List<String>?,
    val riskLevel: String?, // LOW, MEDIUM, HIGH
    val error: String?
)