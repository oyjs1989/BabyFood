package com.example.babyfood.domain.model

/**
 * 营养摄入数据模型
 * 用于追踪宝宝每日营养摄入情况
 */
data class NutritionIntake(
    val calories: Float = 0f,      // 已摄入热量 (kcal)
    val protein: Float = 0f,       // 已摄入蛋白质 (g)
    val calcium: Float = 0f,       // 已摄入钙 (mg)
    val iron: Float = 0f,          // 已摄入铁 (mg)
    val feedbackCount: Int = 0     // 已反馈餐次数
) {
    /**
     * 计算摄入比例（相对于目标值）
     */
    fun calculateProgress(goal: NutritionGoal): NutritionProgress {
        return NutritionProgress(
            caloriesProgress = if (goal.calories > 0) calories / goal.calories else 0f,
            proteinProgress = if (goal.protein > 0) protein / goal.protein else 0f,
            calciumProgress = if (goal.calcium > 0) calcium / goal.calcium else 0f,
            ironProgress = if (goal.iron > 0) iron / goal.iron else 0f
        )
    }

    companion object {
        /**
         * 创建空的营养摄入数据
         */
        fun empty() = NutritionIntake()
    }
}

/**
 * 营养摄入进度模型
 */
data class NutritionProgress(
    val caloriesProgress: Float,   // 热量进度（0.0-1.0）
    val proteinProgress: Float,    // 蛋白质进度（0.0-1.0）
    val calciumProgress: Float,    // 钙进度（0.0-1.0）
    val ironProgress: Float        // 铁进度（0.0-1.0）
) {
    /**
     * 获取平均进度
     */
    fun getAverageProgress(): Float {
        return (caloriesProgress + proteinProgress + calciumProgress + ironProgress) / 4f
    }
}

/**
 * 反馈状态对应的摄入比例
 */
object FeedbackRatio {
    const val FINISHED = 1.0f      // 光盘 = 100%
    const val HALF = 0.5f          // 吃了一半 = 50%
    const val DISLIKED = 0.0f      // 吐了/不爱吃 = 0%
    const val ALLERGY = 0.0f       // 过敏 = 0%

    /**
     * 根据反馈状态获取摄入比例
     */
    fun getRatio(feedbackStatus: String?): Float {
        return when (feedbackStatus) {
            "FINISHED" -> FINISHED
            "HALF" -> HALF
            "DISLIKED" -> DISLIKED
            "ALLERGY" -> ALLERGY
            else -> 0f
        }
    }
}