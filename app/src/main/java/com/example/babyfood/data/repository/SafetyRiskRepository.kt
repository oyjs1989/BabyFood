package com.example.babyfood.data.repository

import android.util.Log
import com.example.babyfood.data.local.database.dao.SafetyRiskDao
import com.example.babyfood.data.local.database.entity.SafetyRiskEntity
import com.example.babyfood.domain.model.RiskLevel
import com.example.babyfood.domain.model.SafetyRisk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 安全风险仓库
 *
 * 负责安全风险数据的查询和管理
 */
@Singleton
class SafetyRiskRepository @Inject constructor(
    private val safetyRiskDao: SafetyRiskDao
) {
    companion object {
        private const val TAG = "SafetyRiskRepository"
    }

    /**
     * 根据食材名称获取安全风险
     */
    suspend fun getByIngredientName(ingredientName: String): SafetyRisk? {
        android.util.Log.d(TAG, "========== 开始查询食材安全风险 ==========")
        android.util.Log.d(TAG, "食材名称: $ingredientName")

        val entity = safetyRiskDao.getByIngredientName(ingredientName)

        val result = entity?.let { SafetyRisk.fromEntity(it) }

        if (result != null) {
            android.util.Log.d(TAG, "✓ 查询成功，风险等级: ${result.riskLevel}")
        } else {
            android.util.Log.d(TAG, "✓ 查询成功，无安全风险记录")
        }
        android.util.Log.d(TAG, "========== 查询完成 ==========")

        return result
    }

    /**
     * 获取所有绝对禁用食材
     */
    suspend fun getAllForbiddenIngredients(): List<SafetyRisk> {
        android.util.Log.d(TAG, "========== 开始查询所有禁用食材 ==========")

        val entities = safetyRiskDao.getAllForbiddenIngredients()
        val result = entities.map { SafetyRisk.fromEntity(it) }

        android.util.Log.d(TAG, "✓ 查询成功，找到 ${result.size} 种禁用食材")
        android.util.Log.d(TAG, "========== 查询完成 ==========")

        return result
    }

    /**
     * 根据月龄获取适用的安全风险
     */
    suspend fun getRisksByAge(ageInMonths: Int): List<SafetyRisk> {
        android.util.Log.d(TAG, "========== 开始查询适用月龄的安全风险 ==========")
        android.util.Log.d(TAG, "宝宝月龄: $ageInMonths")

        val entities = safetyRiskDao.getRisksByAge(ageInMonths)
        val result = entities.map { SafetyRisk.fromEntity(it) }

        android.util.Log.d(TAG, "✓ 查询成功，找到 ${result.size} 条风险记录")
        android.util.Log.d(TAG, "========== 查询完成 ==========")

        return result
    }

    /**
     * 获取所有安全风险
     */
    suspend fun getAllRisks(): List<SafetyRisk> {
        android.util.Log.d(TAG, "========== 开始查询所有安全风险 ==========")

        val entities = safetyRiskDao.getAllRisks()
        val result = entities.map { SafetyRisk.fromEntity(it) }

        android.util.Log.d(TAG, "✓ 查询成功，找到 ${result.size} 条风险记录")
        android.util.Log.d(TAG, "========== 查询完成 ==========")

        return result
    }

    /**
     * 批量查询食材的安全风险
     */
    suspend fun getRisksByIngredientNames(ingredientNames: List<String>): Map<String, SafetyRisk?> {
        android.util.Log.d(TAG, "========== 开始批量查询食材安全风险 ==========")
        android.util.Log.d(TAG, "食材数量: ${ingredientNames.size}")

        val resultMap = ingredientNames.associateWith { name ->
            getByIngredientName(name)
        }

        android.util.Log.d(TAG, "✓ 批量查询完成")
        android.util.Log.d(TAG, "========== 查询完成 ==========")

        return resultMap
    }

    /**
     * 筛选高风险食材（FORBIDDEN 或 NOT_RECOMMENDED）
     */
    suspend fun getHighRiskIngredients(ingredientNames: List<String>): List<SafetyRisk> {
        android.util.Log.d(TAG, "========== 开始筛选高风险食材 ==========")
        android.util.Log.d(TAG, "食材数量: ${ingredientNames.size}")

        val risks = getRisksByIngredientNames(ingredientNames)
        val highRisks = risks.values
            .filterNotNull()
            .filter { it.isHighRisk() }

        android.util.Log.d(TAG, "✓ 筛选完成，找到 ${highRisks.size} 种高风险食材")
        android.util.Log.d(TAG, "========== 筛选完成 ==========")

        return highRisks
    }

    /**
     * 筛选需要特殊处理的食材
     */
    suspend fun getSpecialHandlingIngredients(ingredientNames: List<String>): List<SafetyRisk> {
        android.util.Log.d(TAG, "========== 开始筛选需特殊处理的食材 ==========")
        android.util.Log.d(TAG, "食材数量: ${ingredientNames.size}")

        val risks = getRisksByIngredientNames(ingredientNames)
        val specialHandling = risks.values
            .filterNotNull()
            .filter { it.requiresSpecialHandling() }

        android.util.Log.d(TAG, "✓ 筛选完成，找到 ${specialHandling.size} 种需特殊处理的食材")
        android.util.Log.d(TAG, "========== 筛选完成 ==========")

        return specialHandling
    }

    /**
     * 插入安全风险数据（用于初始化）
     */
    suspend fun insertRisk(
        ingredientName: String,
        riskLevel: RiskLevel,
        riskReason: String,
        handlingAdvice: String?,
        applicableAgeRangeStart: Int?,
        applicableAgeRangeEnd: Int?,
        severity: Int,
        dataSource: String
    ): Long {
        android.util.Log.d(TAG, "========== 开始插入安全风险数据 ==========")
        android.util.Log.d(TAG, "食材名称: $ingredientName, 风险等级: $riskLevel")

        val id = safetyRiskDao.insert(
            ingredientName = ingredientName,
            riskLevel = riskLevel.name,
            riskReason = riskReason,
            handlingAdvice = handlingAdvice,
            applicableAgeRangeStart = applicableAgeRangeStart,
            applicableAgeRangeEnd = applicableAgeRangeEnd,
            severity = severity,
            dataSource = dataSource,
            createdAt = System.currentTimeMillis()
        )

        android.util.Log.d(TAG, "✓ 插入成功，ID: $id")
        android.util.Log.d(TAG, "========== 插入完成 ==========")

        return id
    }
}