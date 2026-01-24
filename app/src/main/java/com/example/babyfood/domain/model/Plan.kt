package com.example.babyfood.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    val id: Long = 0,
    val babyId: Long,
    val recipeId: Long,
    val plannedDate: LocalDate,
    val mealPeriod: String,  // 序列化为字符串
    val status: PlanStatus = PlanStatus.PLANNED,
    val notes: String? = null
)

@Serializable
enum class PlanStatus {
    PLANNED,   // 已计划
    TRIED,     // 已尝试
    SKIPPED    // 已跳过
}