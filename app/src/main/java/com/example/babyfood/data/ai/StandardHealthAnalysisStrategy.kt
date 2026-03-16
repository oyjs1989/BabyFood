package com.example.babyfood.data.ai

import android.util.Log
import com.example.babyfood.data.remote.api.HealthAnalysisApiService
import com.example.babyfood.data.remote.dto.HealthAnalysisRequest
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.HealthRecord
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 标准健康分析策略
 * 直接调用后端健康分析接口 /api/v1/health/analyze
 * 
 * 这是推荐的健康分析策略，使用后端标准 API
 */
@Singleton
class StandardHealthAnalysisStrategy @Inject constructor(
    private val healthAnalysisApiService: HealthAnalysisApiService
) : HealthAnalysisService {

    companion object {
        private const val TAG = "StandardHealthAnalysis"
    }

    /**
     * 分析体检记录
     * 调用后端标准健康分析接口
     */
    override suspend fun analyze(record: HealthRecord, baby: Baby): String? {
        Log.d(TAG, "========== 开始健康分析（标准接口） ==========")
        Log.d(TAG, "宝宝ID: ${baby.id}, 月龄: ${baby.ageInMonths}")
        Log.d(TAG, "体检日期: ${record.recordDate}")

        return try {
            // 构建请求
            val request = HealthAnalysisRequest(
                weight = record.weight?.toFloat(),
                height = record.height?.toFloat(),
                headCircumference = record.headCircumference?.toFloat(),
                ironLevel = record.ironLevel?.toFloat(),
                calciumLevel = record.calciumLevel?.toFloat(),
                hemoglobin = record.hemoglobin?.toFloat(),
                recordDate = record.recordDate.toString(),
                notes = record.notes
            )

            Log.d(TAG, "调用后端健康分析 API...")

            // 调用后端 API
            val response = healthAnalysisApiService.analyzeHealth(request)

            if (response.success) {
                val result = buildAnalysisResult(response)
                Log.d(TAG, "✓ 健康分析成功")
                Log.d(TAG, "风险等级: ${response.riskLevel}")
                Log.d(TAG, "========== 健康分析完成 ==========")
                result
            } else {
                Log.w(TAG, "⚠️ 健康分析返回失败: ${response.error}")
                Log.d(TAG, "========== 健康分析完成（失败） ==========")
                response.error ?: "健康分析失败"
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 健康分析失败: ${e.message}", e)
            Log.d(TAG, "========== 健康分析失败 ==========")
            // 抛出异常，由调用方决定是否降级到本地策略
            throw HealthAnalysisException("健康分析失败: ${e.message}", e)
        }
    }

    /**
     * 构建分析结果字符串
     */
    private fun buildAnalysisResult(response: com.example.babyfood.data.remote.dto.HealthAnalysisResponse): String {
        val builder = StringBuilder()
        
        // 添加结论
        response.conclusion?.let {
            builder.append(it)
        }
        
        // 添加建议
        if (!response.recommendations.isNullOrEmpty()) {
            if (builder.isNotEmpty()) {
                builder.append("\n\n")
            }
            builder.append("建议：\n")
            response.recommendations.forEachIndexed { index, recommendation ->
                builder.append("${index + 1}. $recommendation\n")
            }
        }
        
        // 添加风险等级
        response.riskLevel?.let { level ->
            if (builder.isNotEmpty()) {
                builder.append("\n")
            }
            builder.append("风险等级：${getRiskLevelText(level)}")
        }
        
        return builder.toString().ifEmpty { "健康分析完成" }
    }

    /**
     * 获取风险等级文本
     */
    private fun getRiskLevelText(level: String): String {
        return when (level.uppercase()) {
            "LOW" -> "低风险"
            "MEDIUM" -> "中等风险"
            "HIGH" -> "高风险"
            else -> level
        }
    }
}
