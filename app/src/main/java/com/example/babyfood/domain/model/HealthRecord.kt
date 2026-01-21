package com.example.babyfood.domain.model

import kotlinx.datetime.LocalDate

/**
 * 体检记录
 */
data class HealthRecord(
    val id: Long = 0,
    val babyId: Long,
    val recordDate: LocalDate,           // 体检日期
    val weight: Float?,                  // 体重
    val height: Float?,                  // 身高
    val headCircumference: Float?,       // 头围
    val ironLevel: Float?,               // 铁含量
    val calciumLevel: Float?,            // 钙含量
    val hemoglobin: Float?,              // 血红蛋白
    val aiAnalysis: String?,             // AI 分析结论
    val isConfirmed: Boolean = false,    // 是否已确认
    val expiryDate: LocalDate?,          // 数据有效期
val notes: String?
)

// 扩展函数：将 Domain Model 转换为 Entity
fun HealthRecord.toEntity() = com.example.babyfood.data.local.database.entity.HealthRecordEntity(
    id = id,
    babyId = babyId,
    recordDate = recordDate,
    weight = weight,
    height = height,
    headCircumference = headCircumference,
    ironLevel = ironLevel,
    calciumLevel = calciumLevel,
    hemoglobin = hemoglobin,
    aiAnalysis = aiAnalysis,
    isConfirmed = isConfirmed,
    expiryDate = expiryDate,
    notes = notes
)