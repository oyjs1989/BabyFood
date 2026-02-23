package com.example.babyfood.domain.model

/**
 * 安全风险领域模型
 *
 * 定义食材的安全风险等级和处理建议，用于安全预警系统
 */
data class SafetyRisk(
    val id: Long,
    val ingredientName: String,
    val riskLevel: RiskLevel,
    val riskReason: String,
    val handlingAdvice: String?,
    val applicableAgeRange: IntRange?,
    val severity: Int,  // 1-10
    val dataSource: String
) {
    companion object {
        /**
         * 从 Entity 转换为领域模型
         */
        fun fromEntity(entity: com.example.babyfood.data.local.database.entity.SafetyRiskEntity): SafetyRisk {
            val applicableAgeRange = if (entity.applicableAgeRangeStart != null && entity.applicableAgeRangeEnd != null) {
                entity.applicableAgeRangeStart..entity.applicableAgeRangeEnd
            } else {
                null
            }

            return SafetyRisk(
                id = entity.id,
                ingredientName = entity.ingredientName,
                riskLevel = RiskLevel.valueOf(entity.riskLevel),
                riskReason = entity.riskReason,
                handlingAdvice = entity.handlingAdvice,
                applicableAgeRange = applicableAgeRange,
                severity = entity.severity,
                dataSource = entity.dataSource
            )
        }
    }

    /**
     * 判断是否适用于指定月龄
     */
    fun isApplicableToAge(ageInMonths: Int): Boolean {
        return applicableAgeRange?.contains(ageInMonths) ?: true
    }

    /**
     * 判断是否为高风险（FORBIDDEN 或 NOT_RECOMMENDED）
     */
    fun isHighRisk(): Boolean {
        return riskLevel == RiskLevel.FORBIDDEN || riskLevel == RiskLevel.NOT_RECOMMENDED
    }

    /**
     * 判断是否需要特殊处理
     */
    fun requiresSpecialHandling(): Boolean {
        return riskLevel == RiskLevel.REQUIRES_SPECIAL_HANDLING
    }
}

/**
 * 风险等级枚举
 */
enum class RiskLevel {
    /**
     * 绝对禁用
     * 1岁内严格禁止，可能导致严重健康问题
     */
    FORBIDDEN,

    /**
     * 不推荐
     * 1岁内不推荐，可能影响健康或饮食习惯
     */
    NOT_RECOMMENDED,

    /**
     * 需特殊处理
     * 需要特定烹饪方法才能安全食用
     */
    REQUIRES_SPECIAL_HANDLING,

    /**
     * 需谨慎引入
     * 常见过敏原，需逐个引入并观察反应
     */
    CAUTIOUS_INTRODUCTION,

    /**
     * 正常
     * 安全食材，无特殊限制
     */
    NORMAL
}