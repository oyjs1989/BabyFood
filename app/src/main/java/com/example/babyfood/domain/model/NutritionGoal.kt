package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NutritionGoal(
    val calories: Float,      // kcal
    val protein: Float,       // g
    val calcium: Float,       // mg
    val iron: Float           // mg
) {
    companion object {
        /**
         * 根据月龄计算营养目标（中国营养学会标准）
         */
        fun calculateByAge(ageInMonths: Int): NutritionGoal {
            return when (ageInMonths) {
                in 6..8 -> NutritionGoal(
                    calories = 500f,
                    protein = 20f,
                    calcium = 260f,
                    iron = 8.8f
                )
                in 9..11 -> NutritionGoal(
                    calories = 600f,
                    protein = 25f,
                    calcium = 350f,
                    iron = 9.0f
                )
                in 12..17 -> NutritionGoal(
                    calories = 700f,
                    protein = 30f,
                    calcium = 500f,
                    iron = 9.0f
                )
                in 18..23 -> NutritionGoal(
                    calories = 800f,
                    protein = 35f,
                    calcium = 600f,
                    iron = 9.0f
                )
                else -> NutritionGoal( // 24个月及以上
                    calories = 1000f,
                    protein = 40f,
                    calcium = 800f,
                    iron = 12.0f
                )
            }
        }
    }
}