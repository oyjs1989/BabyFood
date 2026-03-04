package com.example.babyfood.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 健康分析代理响应
 * 从后端 AI 代理返回的分析结果
 */
@Serializable
data class HealthAnalysisProxyResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("analysis")
    val analysis: String? = null,
    
    @SerialName("recommendations")
    val recommendations: List<String>? = null,
    
    @SerialName("riskLevel")
    val riskLevel: String? = null,
    
    @SerialName("error")
    val error: String? = null
)
