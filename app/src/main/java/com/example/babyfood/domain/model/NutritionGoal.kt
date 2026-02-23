package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable
import android.util.Log

@Serializable
data class NutritionGoal(
    val calories: Float,      // kcal
    val protein: Float,       // g
    val calcium: Float,       // mg
    val iron: Float           // mg
) {
    companion object {
        private const val TAG = "NutritionGoal"

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

        /**
         * 智能推荐营养目标（基础推荐 + 体检数据微调）
         * @param ageInMonths 宝宝月龄
         * @param currentWeight 当前体重
         * @param currentHeight 当前身高
         * @param hemoglobin 血红蛋白
         * @param ironLevel 铁含量
         * @param calciumLevel 钙含量
         * @return 智能推荐的营养目标
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
            logInputParameters(ageInMonths, currentWeight, currentHeight, hemoglobin, ironLevel, calciumLevel)

            val goal = calculateByAge(ageInMonths)
            Log.d(TAG, "基础推荐值: $goal")

            val adjustments = calculateAdjustments(
                ageInMonths, currentWeight, currentHeight, hemoglobin, ironLevel, calciumLevel
            )

            val adjustedGoal = applyAdjustments(goal, adjustments)
            logResult(adjustedGoal, adjustments)

            return adjustedGoal
        }

        private fun logInputParameters(
            ageInMonths: Int,
            currentWeight: Float?,
            currentHeight: Float?,
            hemoglobin: Float?,
            ironLevel: Float?,
            calciumLevel: Float?
        ) {
            val parameters = listOf(
                "宝宝月龄" to "$ageInMonths 个月",
                "当前体重" to "${currentWeight?.let { "$it kg" } ?: "无数据"}",
                "当前身高" to "${currentHeight?.let { "$it cm" } ?: "无数据"}",
                "血红蛋白" to "${hemoglobin?.let { "$it g/L" } ?: "无数据"}",
                "铁含量" to "${ironLevel?.let { "$it μmol/L" } ?: "无数据"}",
                "钙含量" to "${calciumLevel?.let { "$it mmol/L" } ?: "无数据"}"
            )

            parameters.forEach { (key, value) ->
                Log.d(TAG, "$key: $value")
            }
        }

        private fun calculateAdjustments(
            ageInMonths: Int,
            currentWeight: Float?,
            currentHeight: Float?,
            hemoglobin: Float?,
            ironLevel: Float?,
            calciumLevel: Float?
        ): NutritionAdjustments {
            var calorieAdjustment = 1.0f
            var proteinAdjustment = 1.0f
            var calciumAdjustment = 1.0f
            var ironAdjustment = 1.0f

            // 根据体重微调热量和蛋白质
            currentWeight?.let { weight ->
                val (minWeight, maxWeight) = getStandardWeightRange(ageInMonths)
                Log.d(TAG, "标准体重范围: $minWeight - $maxWeight kg")

                when {
                    weight < minWeight * 0.9f -> {
                        calorieAdjustment = 1.1f
                        proteinAdjustment = 1.15f
                        Log.d(TAG, "体重偏低，增加热量 10%，蛋白质 15%")
                    }
                    weight > maxWeight * 1.1f -> {
                        calorieAdjustment = 0.9f
                        Log.d(TAG, "体重偏高，减少热量 10%")
                    }
                }
            }

            // 根据身高微调蛋白质
            currentHeight?.let { height ->
                val (minHeight, maxHeight) = getStandardHeightRange(ageInMonths)
                Log.d(TAG, "标准身高范围: $minHeight - $maxHeight cm")

                if (height < minHeight * 0.95f) {
                    proteinAdjustment = (proteinAdjustment * 1.1f).coerceAtMost(1.2f)
                    Log.d(TAG, "身高偏低，增加蛋白质 10%")
                }
            }

            // 根据血红蛋白微调铁
            hemoglobin?.let { hb ->
                val (minHb, _) = getStandardHemoglobinRange(ageInMonths)
                Log.d(TAG, "标准血红蛋白范围: $minHb - 140.0 g/L")

                when {
                    hb < minHb -> {
                        ironAdjustment = 1.2f
                        Log.d(TAG, "血红蛋白偏低（${hb} g/L < ${minHb} g/L），增加铁 20%")
                    }
                    hb < minHb * 1.05f -> {
                        ironAdjustment = 1.1f
                        Log.d(TAG, "血红蛋白略低，轻微增加铁 10%")
                    }
                }
            }

            // 根据铁含量微调铁
            ironLevel?.let { fe ->
                if (fe < 9.0f) {
                    ironAdjustment = (ironAdjustment * 1.1f).coerceAtMost(1.3f)
                    Log.d(TAG, "铁含量偏低（${fe} < 9.0），增加铁 10%")
                }
            }

            // 根据钙含量微调钙
            calciumLevel?.let { ca ->
                when {
                    ca < 2.2f -> {
                        calciumAdjustment = 1.15f
                        Log.d(TAG, "钙含量偏低（${ca} < 2.2），增加钙 15%")
                    }
                    ca < 2.5f -> {
                        calciumAdjustment = 1.08f
                        Log.d(TAG, "钙含量略低，轻微增加钙 8%")
                    }
                }
            }

            return NutritionAdjustments(
                calorieAdjustment,
                proteinAdjustment,
                calciumAdjustment,
                ironAdjustment
            )
        }

        private fun applyAdjustments(
            goal: NutritionGoal,
            adjustments: NutritionAdjustments
        ): NutritionGoal {
            return NutritionGoal(
                calories = (goal.calories * adjustments.calorieAdjustment).toFloat(),
                protein = (goal.protein * adjustments.proteinAdjustment).toFloat(),
                calcium = (goal.calcium * adjustments.calciumAdjustment).toFloat(),
                iron = (goal.iron * adjustments.ironAdjustment).toFloat()
            )
        }

        private fun logResult(goal: NutritionGoal, adjustments: NutritionAdjustments) {
            val adjustmentText = listOf(
                "热量" to adjustments.calorieAdjustment,
                "蛋白质" to adjustments.proteinAdjustment,
                "钙" to adjustments.calciumAdjustment,
                "铁" to adjustments.ironAdjustment
            ).joinToString(", ") { "${it.first} ${String.format("%.2f", it.second)}" }

            Log.d(TAG, "微调系数: $adjustmentText")
            Log.d(TAG, "✓ 智能推荐结果: $goal")
            Log.d(TAG, "========== 智能推荐完成 ==========")
        }

        private data class NutritionAdjustments(
            val calorieAdjustment: Float,
            val proteinAdjustment: Float,
            val calciumAdjustment: Float,
            val ironAdjustment: Float
        )

        /**
         * 获取标准体重范围（WHO 标准）
         * @return Pair(最小值, 最大值)
         */
        private fun getStandardWeightRange(ageInMonths: Int): Pair<Float, Float> {
            return when (ageInMonths) {
                in 6..8 -> Pair(5.7f, 9.2f)
                in 9..11 -> Pair(7.1f, 10.5f)
                in 12..17 -> Pair(8.0f, 12.0f)
                in 18..23 -> Pair(9.0f, 13.5f)
                else -> Pair(10.0f, 16.0f) // 24个月及以上
            }
        }

        /**
         * 获取标准身高范围（WHO 标准）
         * @return Pair(最小值, 最大值)
         */
        private fun getStandardHeightRange(ageInMonths: Int): Pair<Float, Float> {
            return when (ageInMonths) {
                in 6..8 -> Pair(62.0f, 71.0f)
                in 9..11 -> Pair(67.5f, 76.5f)
                in 12..17 -> Pair(72.0f, 83.0f)
                in 18..23 -> Pair(78.0f, 89.0f)
                else -> Pair(85.0f, 98.0f) // 24个月及以上
            }
        }

        /**
         * 获取标准血红蛋白范围（中国标准）
         * @return Pair(最小值, 最大值)
         */
        private fun getStandardHemoglobinRange(ageInMonths: Int): Pair<Float, Float> {
            return when (ageInMonths) {
                in 6..11 -> Pair(110.0f, 140.0f)
                in 12..23 -> Pair(110.0f, 140.0f)
                else -> Pair(110.0f, 140.0f) // 24个月及以上
            }
        }
    }
}