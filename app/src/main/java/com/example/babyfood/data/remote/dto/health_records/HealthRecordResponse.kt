package com.example.babyfood.data.remote.dto.health_records

import kotlinx.serialization.Serializable

/**
 * 健康记录响应 DTO
 */
@Serializable
data class HealthRecordResponse(
    val id: Int,
    val babyId: Int,
    val recordDate: String,
    val weight: Double? = null,
    val height: Double? = null,
    val headCircumference: Double? = null,
    val hemoglobin: Double? = null,
    val iron: Double? = null,
    val calcium: Double? = null,
    val notes: String? = null,
    val createdAt: String
)
