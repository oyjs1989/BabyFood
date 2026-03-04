package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.HealthAnalysisProxyRequest
import com.example.babyfood.data.remote.dto.HealthAnalysisProxyResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * AI 代理 API 服务
 * 通过后端代理调用 AI 服务，避免在前端暴露 API Key
 */
interface AiProxyApiService {

    /**
     * 健康分析代理接口
     * 将健康分析请求转发到后端，由后端调用 AI 服务
     */
    @POST("api/v1/ai/health-analysis")
    suspend fun analyzeHealth(
        @Body request: HealthAnalysisProxyRequest
    ): HealthAnalysisProxyResponse
}
