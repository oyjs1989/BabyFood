package com.example.babyfood.data.ai

/**
 * 健康分析异常
 * 用于标识健康分析过程中的错误，便于调用方进行降级处理
 */
class HealthAnalysisException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
