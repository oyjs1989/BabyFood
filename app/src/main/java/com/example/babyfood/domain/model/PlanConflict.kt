package com.example.babyfood.domain.model

/**
 * 计划冲突信息
 */
data class PlanConflict(
    val newPlan: Plan,
    val existingPlan: Plan,
    val conflictType: ConflictType,
    val existingRecipeName: String = "未知食谱",  // 现有食谱名称
    val newRecipeName: String = "未知食谱"       // 推荐食谱名称
)

/**
 * 冲突类型
 */
enum class ConflictType {
    SAME_DATE_AND_PERIOD,  // 同一天同一餐段
    SAME_RECIPE           // 同一食谱在不同时间
}

/**
 * 冲突解决策略
 */
enum class ConflictResolution {
    OVERWRITE_ALL,    // 覆盖所有冲突
    SKIP_CONFLICTS,   // 跳过冲突，只保存新计划
    CANCEL            // 取消保存
}

/**
 * 保存结果
 */
data class SaveResult(
    val success: Boolean,
    val savedCount: Int = 0,
    val skippedCount: Int = 0,
    val error: String? = null
)