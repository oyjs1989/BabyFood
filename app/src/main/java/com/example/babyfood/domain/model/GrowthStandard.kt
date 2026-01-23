package com.example.babyfood.domain.model

import kotlinx.datetime.LocalDate

/**
 * 生长标准数据（WHO/中国标准）
 */
data class GrowthStandard(
    val ageInMonths: Int,           // 月龄
    val weightP3: Float,            // 体重 P3（第3百分位）
    val weightP50: Float,           // 体重 P50（中位数）
    val weightP97: Float,           // 体重 P97（第97百分位）
    val heightP3: Float,            // 身高 P3
    val heightP50: Float,           // 身高 P50
    val heightP97: Float,           // 身高 P97
    val headCircumferenceP3: Float?, // 头围 P3
    val headCircumferenceP50: Float?, // 头围 P50
    val headCircumferenceP97: Float?  // 头围 P97
) {
    companion object {
        /**
         * WHO 男孩生长标准（体重、身高、头围）
         * 数据来源：WHO Child Growth Standards
         * 包含月龄：0, 1, 2, 3, 4, 6, 9, 12, 15, 18, 21, 24, 30, 36
         */
        val boyGrowthStandards = listOf(
            GrowthStandard(0, 2.5f, 3.3f, 4.4f, 45.6f, 49.9f, 54.4f, 31.9f, 34.5f, 37.2f),
            GrowthStandard(1, 3.4f, 4.5f, 5.8f, 50.0f, 54.6f, 59.0f, 34.0f, 36.6f, 39.2f),
            GrowthStandard(2, 4.2f, 5.6f, 7.2f, 53.2f, 58.1f, 62.9f, 36.0f, 38.4f, 40.9f),
            GrowthStandard(3, 5.0f, 6.4f, 8.0f, 56.1f, 61.0f, 65.7f, 37.4f, 39.6f, 41.9f),
            GrowthStandard(4, 5.6f, 7.0f, 8.7f, 58.3f, 63.0f, 67.7f, 38.3f, 40.5f, 42.7f),
            GrowthStandard(6, 5.7f, 7.9f, 10.8f, 61.4f, 67.6f, 74.0f, 40.6f, 43.3f, 46.1f),
            GrowthStandard(9, 6.8f, 8.8f, 11.3f, 66.6f, 71.3f, 76.1f, 42.3f, 44.7f, 47.2f),
            GrowthStandard(12, 7.7f, 9.6f, 12.0f, 71.0f, 75.7f, 80.5f, 44.2f, 46.1f, 48.0f),
            GrowthStandard(15, 8.2f, 10.0f, 12.4f, 73.7f, 78.7f, 83.7f, 45.0f, 46.7f, 48.5f),
            GrowthStandard(18, 8.8f, 10.4f, 12.7f, 76.5f, 82.3f, 88.0f, 45.8f, 47.3f, 48.9f),
            GrowthStandard(21, 9.2f, 10.9f, 13.2f, 78.9f, 84.8f, 90.7f, 46.3f, 47.7f, 49.2f),
            GrowthStandard(24, 9.7f, 11.5f, 13.9f, 81.9f, 87.8f, 94.0f, 46.9f, 48.3f, 49.8f),
            GrowthStandard(30, 10.5f, 12.4f, 15.0f, 86.2f, 92.6f, 99.3f, 47.6f, 48.9f, 50.3f),
            GrowthStandard(36, 11.2f, 13.3f, 16.2f, 89.9f, 96.8f, 104.0f, 48.1f, 49.4f, 50.8f)
        )

        /**
         * WHO 女孩生长标准（体重、身高、头围）
         * 数据来源：WHO Child Growth Standards
         * 包含月龄：0, 1, 2, 3, 4, 6, 9, 12, 15, 18, 21, 24, 30, 36
         */
        val girlGrowthStandards = listOf(
            GrowthStandard(0, 2.4f, 3.2f, 4.2f, 44.7f, 49.1f, 53.7f, 31.5f, 33.9f, 36.5f),
            GrowthStandard(1, 3.2f, 4.2f, 5.5f, 49.0f, 53.6f, 58.2f, 33.5f, 35.9f, 38.5f),
            GrowthStandard(2, 4.0f, 5.1f, 6.6f, 52.2f, 56.9f, 61.6f, 35.2f, 37.5f, 40.0f),
            GrowthStandard(3, 4.7f, 5.8f, 7.3f, 54.9f, 59.6f, 64.3f, 36.5f, 38.7f, 40.9f),
            GrowthStandard(4, 5.2f, 6.4f, 7.9f, 57.0f, 61.6f, 66.2f, 37.4f, 39.5f, 41.7f),
            GrowthStandard(6, 5.1f, 7.3f, 9.8f, 59.6f, 65.7f, 72.0f, 39.6f, 42.0f, 44.5f),
            GrowthStandard(9, 6.3f, 8.1f, 10.4f, 64.8f, 69.7f, 74.7f, 41.3f, 43.7f, 46.1f),
            GrowthStandard(12, 7.0f, 8.9f, 11.1f, 69.2f, 74.0f, 78.9f, 43.2f, 45.0f, 46.8f),
            GrowthStandard(15, 7.6f, 9.4f, 11.6f, 72.2f, 77.2f, 82.3f, 44.1f, 45.7f, 47.4f),
            GrowthStandard(18, 8.1f, 9.8f, 11.9f, 74.8f, 80.2f, 85.7f, 44.7f, 46.2f, 47.7f),
            GrowthStandard(21, 8.6f, 10.3f, 12.5f, 77.2f, 82.7f, 88.4f, 45.2f, 46.6f, 48.1f),
            GrowthStandard(24, 9.0f, 10.8f, 13.2f, 80.1f, 86.4f, 92.9f, 45.7f, 47.2f, 48.7f),
            GrowthStandard(30, 9.8f, 11.7f, 14.3f, 84.8f, 91.0f, 97.8f, 46.5f, 47.9f, 49.4f),
            GrowthStandard(36, 10.5f, 12.5f, 15.4f, 88.4f, 95.3f, 102.5f, 47.1f, 48.5f, 50.0f)
        )

        /**
         * 根据性别和月龄获取标准数据
         */
        fun getStandard(isBoy: Boolean, ageInMonths: Int): GrowthStandard? {
            val standards = if (isBoy) boyGrowthStandards else girlGrowthStandards
            return standards.find { it.ageInMonths == ageInMonths }
                ?: standards.minByOrNull { kotlin.math.abs(it.ageInMonths - ageInMonths) }
        }

        /**
         * 生成平滑的标准曲线数据（线性插值）
         */
        fun generateSmoothCurve(
            isBoy: Boolean,
            startAge: Int,
            endAge: Int
        ): List<GrowthStandard> {
            val standards = if (isBoy) boyGrowthStandards else girlGrowthStandards
            val result = mutableListOf<GrowthStandard>()
            
            for (age in startAge..endAge) {
                // 找到相邻的两个标准数据点
                val lower = standards.lastOrNull { it.ageInMonths <= age }
                val upper = standards.firstOrNull { it.ageInMonths >= age }
                
                if (lower == null || upper == null) {
                    // 如果超出范围，使用最近的数据点
                    val nearest = standards.minByOrNull { kotlin.math.abs(it.ageInMonths - age) }
                    if (nearest != null) {
                        result.add(nearest)
                    }
                    continue
                }
                
                if (lower.ageInMonths == upper.ageInMonths) {
                    // 正好是标准数据点
                    result.add(lower)
                } else {
                    // 线性插值
                    val ratio = (age - lower.ageInMonths).toFloat() / 
                               (upper.ageInMonths - lower.ageInMonths)
                    
                    result.add(
                        GrowthStandard(
                            ageInMonths = age,
                            weightP3 = lower.weightP3 + (upper.weightP3 - lower.weightP3) * ratio,
                            weightP50 = lower.weightP50 + (upper.weightP50 - lower.weightP50) * ratio,
                            weightP97 = lower.weightP97 + (upper.weightP97 - lower.weightP97) * ratio,
                            heightP3 = lower.heightP3 + (upper.heightP3 - lower.heightP3) * ratio,
                            heightP50 = lower.heightP50 + (upper.heightP50 - lower.heightP50) * ratio,
                            heightP97 = lower.heightP97 + (upper.heightP97 - lower.heightP97) * ratio,
                            headCircumferenceP3 = if (lower.headCircumferenceP3 != null && upper.headCircumferenceP3 != null) {
                                lower.headCircumferenceP3 + (upper.headCircumferenceP3 - lower.headCircumferenceP3) * ratio
                            } else null,
                            headCircumferenceP50 = if (lower.headCircumferenceP50 != null && upper.headCircumferenceP50 != null) {
                                lower.headCircumferenceP50 + (upper.headCircumferenceP50 - lower.headCircumferenceP50) * ratio
                            } else null,
                            headCircumferenceP97 = if (lower.headCircumferenceP97 != null && upper.headCircumferenceP97 != null) {
                                lower.headCircumferenceP97 + (upper.headCircumferenceP97 - lower.headCircumferenceP97) * ratio
                            } else null
                        )
                    )
                }
            }
            
            return result
        }

        /**
         * 根据月龄范围生成标准曲线数据
         */
        fun generateStandardCurveForAgeRange(
            isBoy: Boolean,
            startAge: Int,
            endAge: Int
        ): List<GrowthStandard> {
            return generateSmoothCurve(isBoy, startAge, endAge)
        }
    }
}