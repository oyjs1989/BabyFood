package com.example.babyfood.domain.model

import kotlinx.datetime.LocalDate

/**
 * 食材尝试记录
 *
 * 记录宝宝尝试过的食材及反应，用于追踪味觉发育和预防挑食
 */
data class IngredientTrial(
    val id: Long,
    val babyId: Long,
    val ingredientName: String,
    val trialDate: LocalDate,
    val isAllergic: Boolean,
    val reaction: String?,
    val preference: Preference? = null
) {
    companion object {
        /**
         * 从 Entity 转换为领域模型
         */
        fun fromEntity(entity: com.example.babyfood.data.local.database.entity.IngredientTrialEntity): IngredientTrial {
            return IngredientTrial(
                id = entity.id,
                babyId = entity.babyId,
                ingredientName = entity.ingredientName,
                trialDate = LocalDate.fromEpochDays((entity.trialDate / 86400000).toInt()),
                isAllergic = entity.isAllergic,
                reaction = entity.reaction,
                preference = null
            )
        }

        /**
         * 转换为 Entity
         */
        fun toEntity(trial: IngredientTrial): com.example.babyfood.data.local.database.entity.IngredientTrialEntity {
            return com.example.babyfood.data.local.database.entity.IngredientTrialEntity(
                id = trial.id,
                babyId = trial.babyId,
                ingredientName = trial.ingredientName,
                trialDate = trial.trialDate.toEpochDays().toLong() * 86400000,
                isAllergic = trial.isAllergic,
                reaction = trial.reaction
            )
        }
    }

    /**
     * 判断是否成功接受
     */
    fun isAccepted(): Boolean {
        return !isAllergic && preference == Preference.LIKE
    }

    /**
     * 判断是否需要观察
     */
    fun needsObservation(): Boolean {
        return preference == Preference.NEUTRAL || reaction != null
    }
}

/**
 * 偏好程度枚举
 */
enum class Preference {
    LIKE,        // 喜欢
    NEUTRAL,     // 中立
    DISLIKE      // 不喜欢
}