package com.example.babyfood.data.remote.dto.growth_records

import kotlinx.serialization.Serializable

/**
 * 生长记录响应 DTO
 */
@Serializable
data class GrowthRecordResponse(
    val id: Int,
    val babyId: Int,
    val recordDate: String,
    val weight: Double,
    val height: Double,
    val headCircumference: Double? = null,
    val notes: String? = null,
    val createdAt: String
)
