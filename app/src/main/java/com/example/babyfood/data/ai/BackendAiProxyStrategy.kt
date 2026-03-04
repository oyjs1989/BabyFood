package com.example.babyfood.data.ai

import android.util.Log
import com.example.babyfood.data.remote.api.AiProxyApiService
import com.example.babyfood.data.remote.dto.HealthAnalysisProxyRequest
import com.example.babyfood.data.remote.dto.HealthRecordDto
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.HealthRecord
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 后端代理健康分析策略
 * 通过后端代理调用 AI 服务，避免在前端暴露 API Key
 * 
 * 这是推荐的健康分析策略，符合安全最佳实践
 */
@Singleton
class BackendAiProxyStrategy @Inject constructor(
    private val aiProxyApiService: AiProxyApiService
) : HealthAnalysisService {

    companion object {
        private const val TAG = "BackendAiProxyStrategy"
    }

    /**
     * 分析体检记录
     * 通过后端代理调用 AI 服务
     */
    override suspend fun analyze(record: HealthRecord, baby: Baby): String? {
        Log.d(TAG, "========== 开始健康分析（后端代理） ==========")
        Log.d(TAG, "宝宝ID: ${baby.id}, 月龄: ${baby.ageInMonths}")
        Log.d(TAG, "体检日期: ${record.recordDate}")

        return try {
            // 构建请求 - 使用远程 DTO 包中的 HealthRecordDto
            val request = HealthAnalysisProxyRequest(
                babyId = baby.id,
                healthRecord = HealthRecordDto(
                    weight = record.weight?.toDouble(),
                    height = record.height?.toDouble(),
                    headCircumference = record.headCircumference?.toDouble(),
                    hemoglobin = record.hemoglobin?.toDouble(),
                    iron = record.ironLevel?.toDouble(),
                    calcium = record.calciumLevel?.toDouble(),
                    recordDate = record.recordDate.toString()
                )
            )

            Log.d(TAG, "调用后端代理 API...")

            // 调用后端代理 API
            val response = aiProxyApiService.analyzeHealth(request)

            if (response.success && response.analysis != null) {
                Log.d(TAG, "✓ 健康分析成功")
                Log.d(TAG, "风险等级: ${response.riskLevel}")
                Log.d(TAG, "========== 健康分析完成 ==========")
                response.analysis
            } else {
                Log.w(TAG, "⚠️ 健康分析返回空结果")
                Log.d(TAG, "========== 健康分析完成（无结果） ==========")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ 后端代理健康分析失败: ${e.message}", e)
            Log.d(TAG, "========== 健康分析失败 ==========")
            // 抛出异常，由调用方决定是否降级到本地策略
            throw HealthAnalysisException("后端代理健康分析失败: ${e.message}", e)
        }
    }
}