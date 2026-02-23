package com.example.babyfood.data.remote.dto.growth_records

import kotlinx.serialization.Serializable

/**
 * 创建生长记录请求 DTO
 */
@Serializable
data class GrowthRecordCreate(
    val recordDate: String,
    val weight: Double,
    val height: Double,
    val headCircumference: Double? = null,
    val notes: String? = null
)
