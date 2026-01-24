package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.HealthAnalysisRequest
import com.example.babyfood.data.remote.dto.HealthAnalysisResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 健康分析 API 服务接口
 */
interface HealthAnalysisApiService {

    /**
     * 分析健康数据
     * @param request 健康分析请求
     * @return 健康分析响应
     */
    @POST("api/v1/health/analyze")
    suspend fun analyzeHealth(
        @Body request: HealthAnalysisRequest
    ): HealthAnalysisResponse
}