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
    val notes: String? = null,
    val mealTime: String? = null,  // 用户自定义的用餐时间（格式：HH:mm）
    val feedbackStatus: String? = null,  // 反馈状态（FINISHED、HALF、REJECTED、ALLERGY）
    val feedbackTime: String? = null  // 反馈时间（ISO 8601 格式）
)

@Serializable
enum class PlanStatus {
    PLANNED,   // 已计划
    TRIED,     // 已尝试
    SKIPPED    // 已跳过
}

/**
 * 用餐反馈选项枚举
 */
enum class MealFeedbackOption(val displayName: String, val value: String) {
    FINISHED("光盘", "FINISHED"),
    HALF("吃了一半", "HALF"),
    DISLIKED("吐了/不爱吃", "DISLIKED"),
    ALLERGY("出现过敏", "ALLERGY");

    companion object {
        fun fromValue(value: String?): MealFeedbackOption? {
            return values().find { it.value == value }
        }
    }
}