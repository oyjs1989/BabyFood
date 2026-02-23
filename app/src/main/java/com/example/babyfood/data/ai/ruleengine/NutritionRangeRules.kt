package com.example.babyfood.data.ai.ruleengine

import com.example.babyfood.domain.model.Nutrition

/**
 * 营养素范围规则
 * 基于中国营养学会《中国居民膳食营养素参考摄入量》
 */
object NutritionRangeRules {

    /**
     * 每日营养素范围（按年龄段）
     */
    data class NutritionRange(
        val minCalories: Double,
        val maxCalories: Double,
        val minProtein: Double,
        val maxProtein: Double,
        val minCalcium: Double,
        val maxCalcium: Double,
        val minIron: Double,
        val maxIron: Double
    )

    private val ranges = mapOf(
        // 6-12个月
        Pair(6, 12) to NutritionRange(
            minCalories = 500.0,
            maxCalories = 800.0,
            minProtein = 15.0,
            maxProtein = 25.0,
            minCalcium = 200.0,
            maxCalcium = 300.0,
            minIron = 8.0,
            maxIron = 12.0
        ),

        // 12-24个月
        Pair(12, 24) to NutritionRange(
            minCalories = 900.0,
            maxCalories = 1200.0,
            minProtein = 25.0,
            maxProtein = 35.0,
            minCalcium = 400.0,
            maxCalcium = 600.0,
            minIron = 9.0,
            maxIron = 13.0
        ),

        // 24-36个月
        Pair(24, 36) to NutritionRange(
            minCalories = 1100.0,
            maxCalories = 1400.0,
            minProtein = 30.0,
            maxProtein = 45.0,
            minCalcium = 600.0,
            maxCalcium = 800.0,
            minIron = 10.0,
            maxIron = 14.0
        )
    )

    /**
     * 获取指定年龄段的营养素范围
     */
    fun getRange(ageInMonths: Int): NutritionRange? {
        return when {
            ageInMonths < 6 -> null  // 6个月以下不需要辅食营养计算
            ageInMonths < 12 -> ranges[Pair(6, 12)]
            ageInMonths < 24 -> ranges[Pair(12, 24)]
            ageInMonths < 36 -> ranges[Pair(24, 36)]
            else -> ranges[Pair(24, 36)]  // 36个月以上使用24-36个月的标准
        }
    }

    /**
     * 检查营养素是否在合理范围内
     */
    fun isInRange(nutrition: Nutrition, ageInMonths: Int): Boolean {
        val range = getRange(ageInMonths) ?: return true

        return (nutrition.calories?.toDouble() ?: 0.0) in range.minCalories..range.maxCalories &&
               (nutrition.protein?.toDouble() ?: 0.0) in range.minProtein..range.maxProtein &&
               (nutrition.calcium?.toDouble() ?: 0.0) in range.minCalcium..range.maxCalcium &&
               (nutrition.iron?.toDouble() ?: 0.0) in range.minIron..range.maxIron
    }

    /**
     * 获取营养素评估结果
     */
    fun evaluateNutrition(nutrition: Nutrition, ageInMonths: Int): NutritionEvaluation {
        val range = getRange(ageInMonths) ?: return NutritionEvaluation(
            isInRange = true,
            caloriesStatus = NutritionStatus.UNKNOWN,
            proteinStatus = NutritionStatus.UNKNOWN,
            calciumStatus = NutritionStatus.UNKNOWN,
            ironStatus = NutritionStatus.UNKNOWN,
            suggestions = emptyList()
        )

        val caloriesStatus = when {
            (nutrition.calories?.toDouble() ?: 0.0) < range.minCalories -> NutritionStatus.LOW
            (nutrition.calories?.toDouble() ?: 0.0) > range.maxCalories -> NutritionStatus.HIGH
            else -> NutritionStatus.NORMAL
        }

        val proteinStatus = when {
            (nutrition.protein?.toDouble() ?: 0.0) < range.minProtein -> NutritionStatus.LOW
            (nutrition.protein?.toDouble() ?: 0.0) > range.maxProtein -> NutritionStatus.HIGH
            else -> NutritionStatus.NORMAL
        }

        val calciumStatus = when {
            (nutrition.calcium?.toDouble() ?: 0.0) < range.minCalcium -> NutritionStatus.LOW
            (nutrition.calcium?.toDouble() ?: 0.0) > range.maxCalcium -> NutritionStatus.HIGH
            else -> NutritionStatus.NORMAL
        }

        val ironStatus = when {
            (nutrition.iron?.toDouble() ?: 0.0) < range.minIron -> NutritionStatus.LOW
            (nutrition.iron?.toDouble() ?: 0.0) > range.maxIron -> NutritionStatus.HIGH
            else -> NutritionStatus.NORMAL
        }

        val suggestions = buildList {
            if (caloriesStatus == NutritionStatus.LOW) {
                add("热量偏低，可适当增加主食或健康脂肪摄入")
            } else if (caloriesStatus == NutritionStatus.HIGH) {
                add("热量偏高，建议减少高热量食物")
            }

            if (proteinStatus == NutritionStatus.LOW) {
                add("蛋白质偏低，可增加肉类、蛋类、豆制品摄入")
            } else if (proteinStatus == NutritionStatus.HIGH) {
                add("蛋白质偏高，建议适当减少蛋白质来源食物")
            }

            if (calciumStatus == NutritionStatus.LOW) {
                add("钙摄入不足，建议增加奶制品、豆制品、深绿色蔬菜")
            } else if (calciumStatus == NutritionStatus.HIGH) {
                add("钙摄入偏高，建议适当减少奶制品")
            }

            if (ironStatus == NutritionStatus.LOW) {
                add("铁摄入不足，建议增加红肉、动物肝脏、深绿色蔬菜，搭配维生素C促进吸收")
            } else if (ironStatus == NutritionStatus.HIGH) {
                add("铁摄入偏高，建议适当减少红肉和肝脏类食物")
            }
        }

        return NutritionEvaluation(
            isInRange = isInRange(nutrition, ageInMonths),
            caloriesStatus = caloriesStatus,
            proteinStatus = proteinStatus,
            calciumStatus = calciumStatus,
            ironStatus = ironStatus,
            suggestions = suggestions
        )
    }
}

/**
 * 营养素状态
 */
enum class NutritionStatus {
    LOW,      // 偏低
    NORMAL,   // 正常
    HIGH,     // 偏高
    UNKNOWN   // 未知
}

/**
 * 营养评估结果
 */
data class NutritionEvaluation(
    val isInRange: Boolean,
    val caloriesStatus: NutritionStatus,
    val proteinStatus: NutritionStatus,
    val calciumStatus: NutritionStatus,
    val ironStatus: NutritionStatus,
    val suggestions: List<String>
)