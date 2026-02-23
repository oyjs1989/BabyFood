package com.example.babyfood.data.remote.dto.health_records

import kotlinx.serialization.Serializable

/**
 * 创建健康记录请求 DTO
 */
@Serializable
data class HealthRecordCreate(
    val recordDate: String,
    val weight: Double? = null,
    val height: Double? = null,
    val headCircumference: Double? = null,
    val hemoglobin: Double? = null,
    val iron: Double? = null,
    val calcium: Double? = null,
    val notes: String? = null
)
