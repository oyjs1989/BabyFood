package com.example.babyfood.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Baby(
    val id: Long = 0,
    val name: String,
    val birthDate: LocalDate,
    val allergies: List<AllergyItem> = emptyList(),  // 过敏食材列表（带有效期）
    val weight: Float? = null,      // kg
    val height: Float? = null,      // cm
    val preferences: List<PreferenceItem> = emptyList(),  // 偏好食材列表（带有效期）
    val nutritionGoal: NutritionGoal? = null,  // 自定义营养目标（用户可调整）
    val avatarUrl: String? = null   // 头像 URL
) {
    val ageInMonths: Int
        get() {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val months = (today.year - birthDate.year) * 12 + (today.monthNumber - birthDate.monthNumber)
            return if (today.dayOfMonth >= birthDate.dayOfMonth) months else months - 1
        }

    /**
     * 获取有效的过敏食材列表（排除已过期的）
     */
    fun getEffectiveAllergies(): List<String> {
        return allergies.filterNot { it.isExpired() }.map { it.ingredient }
    }

    /**
     * 获取有效的偏好食材列表（排除已过期的）
     */
    fun getEffectivePreferences(): List<String> {
        return preferences.filterNot { it.isExpired() }.map { it.ingredient }
    }

    /**
     * 获取营养目标（优先使用自定义目标，否则根据月龄计算）
     */
    fun getEffectiveNutritionGoal(): NutritionGoal {
        return nutritionGoal ?: NutritionGoal.calculateByAge(ageInMonths)
    }
}