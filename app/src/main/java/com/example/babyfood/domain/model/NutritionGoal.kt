package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable
import android.util.Log

@Serializable
data class NutritionGoal(
    val calories: Float,      // kcal
    val protein: Float,       // g
    val calcium: Float,       // mg
    val iron: Float,          // mg
    val vitaminA: Float = 0f, // μg
    val vitaminC: Float = 0f  // mg
) {
    companion object {
        private const val TAG = "NutritionGoal"

        /**
         * 根据月龄计算营养目标（中国营养学会标准）
         */
        fun calculateByAge(ageInMonths: Int): NutritionGoal {
            return when (ageInMonths) {
                in 6..8 -> NutritionGoal(
                    calories = 550f,
                    protein = 9f,
                    calcium = 250f,
                    iron = 8.8f,
                    vitaminA = 350f,
                    vitaminC = 40f
                )
                in 9..11 -> NutritionGoal(
                    calories = 700f,
                    protein = 12f,
                    calcium = 400f,
                    iron = 9.0f,
                    vitaminA = 350f,
                    vitaminC = 40f
                )
                in 12..17 -> NutritionGoal(
                    calories = 1000f,
                    protein = 25f,
                    calcium = 600f,
                    iron = 9.0f,
                    vitaminA = 350f,
                    vitaminC = 40f
                )
                in 18..23 -> NutritionGoal(
                    calories = 1100f,
                    protein = 25f,
                    calcium = 600f,
                    iron = 9.0f,
                    vitaminA = 400f,
                    vitaminC = 40f
                )
                else -> NutritionGoal( // 24个月及以上
                    calories = 1200f,
                    protein = 25f,
                    calcium = 600f,
                    iron = 9.0f,
                    vitaminA = 400f,
                    vitaminC = 40f
                )
            }
        }

        /**
         * 智能推荐营养目标（基础推荐 + 体检数据微调）
         */
        fun calculateWithHealthData(
            ageInMonths: Int,
            currentWeight: Float? = null,
            currentHeight: Float? = null,
            hemoglobin: Float? = null,
            ironLevel: Float? = null,
            calciumLevel: Float? = null
        ): NutritionGoal {
            Log.d(TAG, "========== 开始智能推荐营养目标 ==========")
            
            val goal = calculateByAge(ageInMonths)
            Log.d(TAG, "基础推荐值: $goal")

            var calorieAdjustment = 1.0f
            var proteinAdjustment = 1.0f
            var calciumAdjustment = 1.0f
            var ironAdjustment = 1.0f

            // 根据体重微调热量和蛋白质
            currentWeight?.let { weight ->
                val (minWeight, maxWeight) = getStandardWeightRange(ageInMonths)
                when {
                    weight < minWeight * 0.9f -> {
                        calorieAdjustment = 1.1f
                        proteinAdjustment = 1.15f
                    }
                    weight > maxWeight * 1.1f -> {
                        calorieAdjustment = 0.9f
                    }
                }
            }

            // 根据身高微调蛋白质
            currentHeight?.let { height ->
                val (minHeight, _) = getStandardHeightRange(ageInMonths)
                if (height < minHeight * 0.95f) {
                    proteinAdjustment = (proteinAdjustment * 1.1f).coerceAtMost(1.2f)
                }
            }

            // 根据血红蛋白微调铁
            hemoglobin?.let { hb ->
                val (minHb, _) = getStandardHemoglobinRange(ageInMonths)
                if (hb < minHb) {
                    ironAdjustment = 1.2f
                }
            }

            // 根据钙含量微调钙
            calciumLevel?.let { ca ->
                if (ca < 2.2f) {
                    calciumAdjustment = 1.15f
                }
            }

            val adjustedGoal = NutritionGoal(
                calories = goal.calories * calorieAdjustment,
                protein = goal.protein * proteinAdjustment,
                calcium = goal.calcium * calciumAdjustment,
                iron = goal.iron * ironAdjustment,
                vitaminA = goal.vitaminA,
                vitaminC = goal.vitaminC
            )

            Log.d(TAG, "✓ 智能推荐结果: $adjustedGoal")
            return adjustedGoal
        }

        /**
         * 获取标准体重范围（WHO 标准）
         */
        private fun getStandardWeightRange(ageInMonths: Int): Pair<Float, Float> {
            return when (ageInMonths) {
                in 6..8 -> Pair(5.7f, 9.2f)
                in 9..11 -> Pair(7.1f, 10.5f)
                in 12..17 -> Pair(8.0f, 12.0f)
                in 18..23 -> Pair(9.0f, 13.5f)
                else -> Pair(10.0f, 16.0f)
            }
        }

        /**
         * 获取标准身高范围（WHO 标准）
         */
        private fun getStandardHeightRange(ageInMonths: Int): Pair<Float, Float> {
            return when (ageInMonths) {
                in 6..8 -> Pair(62.0f, 71.0f)
                in 9..11 -> Pair(67.5f, 76.5f)
                in 12..17 -> Pair(72.0f, 83.0f)
                in 18..23 -> Pair(78.0f, 89.0f)
                else -> Pair(85.0f, 98.0f)
            }
        }

        /**
         * 获取标准血红蛋白范围
         */
        private fun getStandardHemoglobinRange(ageInMonths: Int): Pair<Float, Float> {
            return Pair(110.0f, 140.0f)
        }
    }
}
