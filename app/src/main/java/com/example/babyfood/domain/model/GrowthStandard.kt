package com.example.babyfood.domain.model

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
         * WHO 男孩体重标准（kg）
         */
        val boyWeightStandards = listOf(
            GrowthStandard(0, 2.5f, 3.3f, 4.4f, 45.6f, 49.9f, 54.4f, 31.9f, 34.5f, 37.2f),
            GrowthStandard(6, 5.7f, 7.9f, 10.8f, 61.4f, 67.6f, 74.0f, 40.6f, 43.3f, 46.1f),
            GrowthStandard(12, 7.7f, 9.6f, 12.0f, 71.0f, 75.7f, 80.5f, 44.2f, 46.1f, 48.0f),
            GrowthStandard(18, 8.8f, 10.4f, 12.7f, 76.5f, 82.3f, 88.0f, 45.8f, 47.3f, 48.9f),
            GrowthStandard(24, 9.7f, 11.5f, 13.9f, 81.9f, 87.8f, 94.0f, 46.9f, 48.3f, 49.8f)
        )

        /**
         * WHO 女孩体重标准（kg）
         */
        val girlWeightStandards = listOf(
            GrowthStandard(0, 2.4f, 3.2f, 4.2f, 44.7f, 49.1f, 53.7f, 31.5f, 33.9f, 36.5f),
            GrowthStandard(6, 5.1f, 7.3f, 9.8f, 59.6f, 65.7f, 72.0f, 39.6f, 42.0f, 44.5f),
            GrowthStandard(12, 7.0f, 8.9f, 11.1f, 69.2f, 74.0f, 78.9f, 43.2f, 45.0f, 46.8f),
            GrowthStandard(18, 8.1f, 9.8f, 11.9f, 74.8f, 80.2f, 85.7f, 44.7f, 46.2f, 47.7f),
            GrowthStandard(24, 9.0f, 10.8f, 13.2f, 80.1f, 86.4f, 92.9f, 45.7f, 47.2f, 48.7f)
        )

        /**
         * 根据性别和月龄获取标准数据
         */
        fun getStandard(isBoy: Boolean, ageInMonths: Int): GrowthStandard? {
            val standards = if (isBoy) boyWeightStandards else girlWeightStandards
            return standards.find { it.ageInMonths == ageInMonths }
                ?: standards.minByOrNull { kotlin.math.abs(it.ageInMonths - ageInMonths) }
        }
    }
}