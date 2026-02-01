package com.example.babyfood.data.service

import android.util.Log
import com.example.babyfood.data.repository.SafetyRiskRepository
import com.example.babyfood.data.repository.UserWarningIgnoreRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.RiskLevel
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.domain.model.SafetyRisk
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 安全风险分析服务
 *
 * 实现风险评估算法（5级风险分类），检测食材安全风险并提供处理建议
 */
@Singleton
class SafetyRiskAnalyzer @Inject constructor(
    private val safetyRiskRepository: SafetyRiskRepository,
    private val userWarningIgnoreRepository: UserWarningIgnoreRepository
) {
    companion object {
        private const val TAG = "SafetyRiskAnalyzer"
    }

    /**
     * 食谱安全分析结果
     */
    data class RecipeSafetyAnalysis(
        val recipeId: Long,
        val recipeName: String,
        val hasForbiddenIngredient: Boolean,
        val hasNotRecommendedIngredient: Boolean,
        val hasSpecialHandlingIngredient: Boolean,
        val hasCautiousIntroductionIngredient: Boolean,
        val overallRiskLevel: RiskLevel,
        val risks: List<IngredientRisk>,
        val ignoreHistory: Map<String, Int>,  // 食材名称 -> 忽略次数
        val recommendedAction: String
    )

    /**
     * 食材风险详情
     */
    data class IngredientRisk(
        val ingredientName: String,
        val riskLevel: RiskLevel,
        val riskReason: String,
        val handlingAdvice: String?,
        val severity: Int,
        val ignoreCount: Int  // 用户忽略该警告的次数
    )

    /**
     * 分析食谱的安全风险
     */
    suspend fun analyzeRecipeSafety(
        recipe: Recipe,
        baby: Baby,
        userId: Long? = null
    ): RecipeSafetyAnalysis {
        android.util.Log.d(TAG, "========== 开始分析食谱安全风险 ==========")
        android.util.Log.d(TAG, "食谱ID: ${recipe.id}, 食谱名称: ${recipe.name}")
        android.util.Log.d(TAG, "宝宝月龄: ${baby.ageInMonths}")

        // 提取食谱中的食材列表
        val ingredientNames = extractIngredientNames(recipe)

        // 查询所有食材的安全风险
        val riskMap = safetyRiskRepository.getRisksByIngredientNames(ingredientNames)

        // 筛选有风险的食材
        val risks = mutableListOf<IngredientRisk>()

        // 查询用户忽略警告的历史记录
        val ignoreHistory = mutableMapOf<String, Int>()

        riskMap.forEach { (name, risk) ->
            if (risk != null && risk.isApplicableToAge(baby.ageInMonths)) {
                // 查询用户是否忽略过该警告
                val ignoreCount = userId?.let {
                    userWarningIgnoreRepository.getIgnoreCount(
                        userId = userId,
                        warningType = risk.riskLevel.name,
                        ingredientName = name
                    )
                } ?: 0

                if (ignoreCount > 0) {
                    ignoreHistory[name] = ignoreCount
                }

                risks.add(
                    IngredientRisk(
                        ingredientName = name,
                        riskLevel = risk.riskLevel,
                        riskReason = risk.riskReason,
                        handlingAdvice = risk.handlingAdvice,
                        severity = risk.severity,
                        ignoreCount = ignoreCount
                    )
                )
            }
        }

        // 统计各级风险
        val hasForbidden = risks.any { it.riskLevel == RiskLevel.FORBIDDEN }
        val hasNotRecommended = risks.any { it.riskLevel == RiskLevel.NOT_RECOMMENDED }
        val hasSpecialHandling = risks.any { it.riskLevel == RiskLevel.REQUIRES_SPECIAL_HANDLING }
        val hasCautiousIntroduction = risks.any { it.riskLevel == RiskLevel.CAUTIOUS_INTRODUCTION }

        // 确定总体风险等级
        val overallRiskLevel = calculateOverallRiskLevel(
            hasForbidden, hasNotRecommended, hasSpecialHandling, hasCautiousIntroduction
        )

        // 生成推荐操作
        val recommendedAction = generateRecommendedAction(overallRiskLevel, risks)

        android.util.Log.d(TAG, "✓ 分析完成，总体风险等级: $overallRiskLevel")
        android.util.Log.d(TAG, "✓ 发现 ${risks.size} 种有风险食材")
        android.util.Log.d(TAG, "========== 分析完成 ==========")

        return RecipeSafetyAnalysis(
            recipeId = recipe.id,
            recipeName = recipe.name,
            hasForbiddenIngredient = hasForbidden,
            hasNotRecommendedIngredient = hasNotRecommended,
            hasSpecialHandlingIngredient = hasSpecialHandling,
            hasCautiousIntroductionIngredient = hasCautiousIntroduction,
            overallRiskLevel = overallRiskLevel,
            risks = risks,
            ignoreHistory = ignoreHistory,
            recommendedAction = recommendedAction
        )
    }

    /**
     * 从食谱中提取食材名称
     */
    private fun extractIngredientNames(recipe: Recipe): List<String> {
        val ingredientsStr = when (recipe.ingredients) {
            is List<*> -> {
                @Suppress("UNCHECKED_CAST")
                (recipe.ingredients as List<String>).joinToString(",")
            }
            else -> recipe.ingredients.toString()
        }

        return ingredientsStr
            .split(",", "、", "，")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    /**
     * 计算总体风险等级（按优先级从高到低）
     */
    private fun calculateOverallRiskLevel(
        hasForbidden: Boolean,
        hasNotRecommended: Boolean,
        hasSpecialHandling: Boolean,
        hasCautiousIntroduction: Boolean
    ): RiskLevel {
        // 风险等级优先级列表（从高到低）
        val riskPriorities = listOf(
            RiskLevel.FORBIDDEN to hasForbidden,
            RiskLevel.NOT_RECOMMENDED to hasNotRecommended,
            RiskLevel.REQUIRES_SPECIAL_HANDLING to hasSpecialHandling,
            RiskLevel.CAUTIOUS_INTRODUCTION to hasCautiousIntroduction
        )

        // 返回第一个匹配的风险等级
        return riskPriorities.firstOrNull { it.second }?.first ?: RiskLevel.NORMAL
    }

    /**
     * 生成推荐操作
     */
    private fun generateRecommendedAction(
        overallRiskLevel: RiskLevel,
        risks: List<IngredientRisk>
    ): String {
        return when (overallRiskLevel) {
            RiskLevel.FORBIDDEN -> {
                val ingredients = getFilteredIngredientNames(risks, RiskLevel.FORBIDDEN)
                "该食谱包含禁用食材：$ingredients，请勿使用"
            }
            RiskLevel.NOT_RECOMMENDED -> {
                val ingredients = getFilteredIngredientNames(risks, RiskLevel.NOT_RECOMMENDED)
                "该食谱包含不推荐食材：$ingredients，建议替换"
            }
            RiskLevel.REQUIRES_SPECIAL_HANDLING -> {
                val specialIngredients = risks
                    .filter { it.riskLevel == RiskLevel.REQUIRES_SPECIAL_HANDLING }
                    .map { "${it.ingredientName}（${it.handlingAdvice ?: "需特殊处理"}）" }
                    .joinToString("；")
                "请按照要求处理食材：$specialIngredients"
            }
            RiskLevel.CAUTIOUS_INTRODUCTION -> {
                val ingredients = getFilteredIngredientNames(risks, RiskLevel.CAUTIOUS_INTRODUCTION)
                "该食谱含常见过敏原：$ingredients，首次食用请少量尝试并观察2-3天"
            }
            RiskLevel.NORMAL -> "该食谱食材安全，适合食用"
        }
    }

    /**
     * 获取指定风险等级的食材名称列表
     */
    private fun getFilteredIngredientNames(risks: List<IngredientRisk>, riskLevel: RiskLevel): String {
        return risks
            .filter { it.riskLevel == riskLevel }
            .joinToString("、") { it.ingredientName }
    }

    /**
     * 检查用户是否频繁忽略某个警告，需要加强提醒
     */
    suspend fun shouldStrengthenWarning(
        userId: Long,
        warningType: String,
        ingredientName: String
    ): Boolean {
        android.util.Log.d(TAG, "========== 检查是否需要加强提醒 ==========")
        android.util.Log.d(TAG, "用户ID: $userId, 警告类型: $warningType, 食材: $ingredientName")

        val ignoreCount = userWarningIgnoreRepository.getIgnoreCount(
            userId = userId,
            warningType = warningType,
            ingredientName = ingredientName
        )

        val shouldStrengthen = ignoreCount >= 2

        if (shouldStrengthen) {
            android.util.Log.w(TAG, "⚠️ 用户已忽略 $ignoreCount 次警告，需要加强提醒")
        } else {
            android.util.Log.d(TAG, "✓ 用户忽略 $ignoreCount 次警告，无需加强")
        }
        android.util.Log.d(TAG, "========== 检查完成 ==========")

        return shouldStrengthen
    }

    /**
     * 生成加强版警告消息
     */
    fun generateStrengthenedWarning(
        risk: SafetyRisk,
        ignoreCount: Int
    ): String {
        val warningTitle = when (risk.riskLevel) {
            RiskLevel.FORBIDDEN -> "⚠️ 严重警告"
            RiskLevel.NOT_RECOMMENDED -> "⚠️ 重要提醒"
            RiskLevel.REQUIRES_SPECIAL_HANDLING -> "💡 重要提示"
            RiskLevel.CAUTIOUS_INTRODUCTION -> "⚠️ 谨慎食用"
            RiskLevel.NORMAL -> "✅ 安全"
        }

        val additionalMessage = when {
            ignoreCount >= 3 -> "您已多次忽略此警告，请务必重视！"
            ignoreCount == 2 -> "您已忽略此警告两次，建议重新考虑。"
            else -> ""
        }

        return buildString {
            appendLine(warningTitle)
            appendLine()
            appendLine("食材：${risk.ingredientName}")
            appendLine("风险原因：${risk.riskReason}")
            if (risk.handlingAdvice != null) {
                appendLine("处理建议：${risk.handlingAdvice}")
            }
            if (additionalMessage.isNotEmpty()) {
                appendLine()
                appendLine("【特别提示】$additionalMessage")
            }
        }
    }
}