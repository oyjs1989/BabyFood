package com.example.babyfood.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 健康分析代理请求
 * 用于向后端 AI 代理发送分析请求
 */
@Serializable
data class HealthAnalysisProxyRequest(
    @SerialName("babyId")
    val babyId: Long,
    
    @SerialName("healthRecord")
    val healthRecord: HealthRecordDto
)

/**
 * 健康记录 DTO
 */
@Serializable
data class HealthRecordDto(
    @SerialName("weight")
    val weight: Double? = null,
    
    @SerialName("height")
    val height: Double? = null,
    
    @SerialName("headCircumference")
    val headCircumference: Double? = null,
    
    @SerialName("hemoglobin")
    val hemoglobin: Double? = null,
    
    @SerialName("iron")
    val iron: Double? = null,
    
    @SerialName("calcium")
    val calcium: Double? = null,
    
    @SerialName("recordDate")
    val recordDate: String? = null
)
