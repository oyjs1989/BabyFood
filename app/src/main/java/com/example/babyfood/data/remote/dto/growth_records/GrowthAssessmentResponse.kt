package com.example.babyfood.data.remote.dto.growth_records

import kotlinx.serialization.Serializable

/**
 * 生长评估响应 DTO
 */
@Serializable
data class GrowthAssessmentResponse(
    val babyId: Int,
    val assessmentDate: String,
    val weightPercentile: Double,
    val heightPercentile: Double,
    val headCircumferencePercentile: Double? = null,
    val weightStatus: String,
    val heightStatus: String,
    val recommendations: List<String>
)
