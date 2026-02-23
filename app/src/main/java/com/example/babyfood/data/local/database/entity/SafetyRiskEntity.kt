package com.example.babyfood.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "safety_risks",
    indices = [Index(value = ["ingredientName"])]
)
data class SafetyRiskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ingredientName: String,
    val riskLevel: String,  // FORBIDDEN, NOT_RECOMMENDED, REQUIRES_SPECIAL_HANDLING, CAUTIOUS_INTRODUCTION, NORMAL
    val riskReason: String,
    val handlingAdvice: String?,
    val applicableAgeRangeStart: Int?,
    val applicableAgeRangeEnd: Int?,
    val severity: Int,  // 1-10
    val dataSource: String,  // WHO, 中国营养学会, AAP
    val createdAt: Long = System.currentTimeMillis()
)