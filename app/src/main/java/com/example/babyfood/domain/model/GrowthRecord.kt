package com.example.babyfood.domain.model

import kotlinx.datetime.LocalDate

/**
 * 生长记录（用于绘制生长曲线）
 */
data class GrowthRecord(
    val id: Long = 0,
    val babyId: Long,
    val recordDate: LocalDate,           // 记录日期
    val weight: Float,                   // 体重
    val height: Float,                   // 身高
    val headCircumference: Float? = null, // 头围
    val notes: String? = null            // 备注
)

// 扩展函数：将 Domain Model 转换为 Entity
fun GrowthRecord.toEntity() = com.example.babyfood.data.local.database.entity.GrowthRecordEntity(
    id = id,
    babyId = babyId,
    recordDate = recordDate,
    weight = weight,
    height = height,
    headCircumference = headCircumference,
    notes = notes
)