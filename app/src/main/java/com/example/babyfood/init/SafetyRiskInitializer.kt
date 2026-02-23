package com.example.babyfood.init

import android.content.Context
import android.util.Log
import com.example.babyfood.data.local.database.dao.SafetyRiskDao
import com.example.babyfood.data.local.database.dao.NutritionDataDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

/**
 * 初始化安全风险和营养数据
 * 从assets资源文件加载数据并插入数据库
 */
class SafetyRiskInitializer(
    private val context: Context,
    private val safetyRiskDao: SafetyRiskDao,
    private val nutritionDataDao: NutritionDataDao
) {
    
    companion object {
        private const val TAG = "SafetyRiskInitializer"
    }
    
    suspend fun initialize() {
        Log.d(TAG, "========== 开始初始化营养数据 ==========")
        
        withContext(Dispatchers.IO) {
            try {
                // 加载安全风险数据
                val safetyRisks = loadSafetyRisksFromAssets()
                Log.d(TAG, "加载了 ${safetyRisks.size} 条安全风险数据")
                
                // 加载营养数据
                val nutritionData = loadNutritionDataFromAssets()
                Log.d(TAG, "加载了 ${nutritionData.size} 条营养数据")
                
                // 插入数据库
                insertSafetyRisks(safetyRisks)
                insertNutritionData(nutritionData)
                
                Log.d(TAG, "✓ 营养数据初始化完成")
                Log.d(TAG, "========== 营养数据初始化完成 ==========")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 营养数据初始化失败: ${e.message}", e)
                throw e
            }
        }
    }
    
    private fun loadSafetyRisksFromAssets(): List<SafetyRiskItem> {
        Log.d(TAG, "开始加载安全风险数据文件")

        val inputStream = context.assets.open("nutrition_data/safety_risks.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val safetyRisks = Json.decodeFromString<List<SafetyRiskItem>>(jsonString)

        Log.d(TAG, "✓ 安全风险数据文件加载成功: ${safetyRisks.size} 条记录")
        return safetyRisks
    }
    
    private fun loadNutritionDataFromAssets(): List<NutritionDataItem> {
        Log.d(TAG, "开始加载营养数据文件")

        val inputStream = context.assets.open("nutrition_data/nutrition_data.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val nutritionData = Json.decodeFromString<List<NutritionDataItem>>(jsonString)

        Log.d(TAG, "✓ 营养数据文件加载成功: ${nutritionData.size} 条记录")
        return nutritionData
    }
    
    private suspend fun insertSafetyRisks(safetyRisks: List<SafetyRiskItem>) {
        Log.d(TAG, "开始插入安全风险数据到数据库")
        
        var insertedCount = 0
        safetyRisks.forEach { risk ->
            try {
                safetyRiskDao.insert(
                    ingredientName = risk.ingredientName,
                    riskLevel = risk.riskLevel,
                    riskReason = risk.riskReason,
                    handlingAdvice = risk.handlingAdvice,
                    applicableAgeRangeStart = risk.applicableAgeRangeStart,
                    applicableAgeRangeEnd = risk.applicableAgeRangeEnd,
                    severity = risk.severity,
                    dataSource = risk.dataSource,
                    createdAt = System.currentTimeMillis()
                )
                insertedCount++
            } catch (e: Exception) {
                Log.w(TAG, "插入安全风险数据失败: ${risk.ingredientName}, 原因: ${e.message}")
            }
        }
        
        Log.d(TAG, "✓ 成功插入 $insertedCount/${safetyRisks.size} 条安全风险数据")
    }
    
    private suspend fun insertNutritionData(nutritionData: List<NutritionDataItem>) {
        Log.d(TAG, "开始插入营养数据到数据库")

        var insertedCount = 0
        nutritionData.forEach { data ->
            try {
                val entity = com.example.babyfood.data.local.database.entity.NutritionDataEntity(
                    ingredientName = data.ingredientName,
                    ironContent = data.ironContent,
                    zincContent = data.zincContent,
                    vitaminAContent = data.vitaminAContent,
                    calciumContent = data.calciumContent,
                    vitaminCContent = data.vitaminCContent
                )
                nutritionDataDao.insert(entity)
                insertedCount++
            } catch (e: Exception) {
                Log.w(TAG, "插入营养数据失败: ${data.ingredientName}, 原因: ${e.message}")
            }
        }

        Log.d(TAG, "✓ 成功插入 $insertedCount/${nutritionData.size} 条营养数据")
    }
    
    // 数据传输对象
    @Serializable
    data class SafetyRiskItem(
        val ingredientName: String,
        val riskLevel: String,
        val riskReason: String,
        val handlingAdvice: String?,
        val applicableAgeRangeStart: Int?,
        val applicableAgeRangeEnd: Int?,
        val severity: Int,
        val dataSource: String
    )

    @Serializable
    data class NutritionDataItem(
        val ingredientName: String,
        val ironContent: Double,
        val zincContent: Double,
        val vitaminAContent: Double,
        val calciumContent: Double,
        val vitaminCContent: Double
    )
}