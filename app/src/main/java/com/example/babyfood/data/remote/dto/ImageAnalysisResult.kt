package com.example.babyfood.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 图像分析结果响应
 */
@Serializable
data class ImageAnalysisResult(
    val success: Boolean,
    
    @SerialName("analysisType")
    val analysisType: String,
    
    val result: ImageAnalysisResultData? = null,
    
    @SerialName("skillUsed")
    val skillUsed: String,
    
    @SerialName("processingTime")
    val processingTime: Double,
    
    @SerialName("errorMessage")
    val errorMessage: String? = null
)

/**
 * 图像分析结果数据
 */
@Serializable
data class ImageAnalysisResultData(
    // 食谱识别结果
    @SerialName("foodName")
    val foodName: String? = null,
    
    val ingredients: List<String>? = null,
    
    @SerialName("minAgeMonths")
    val minAgeMonths: Int? = null,
    
    @SerialName("maxAgeMonths")
    val maxAgeMonths: Int? = null,
    
    val nutrition: NutritionData? = null,
    
    val description: String? = null,
    
    @SerialName("cookingTips")
    val cookingTips: String? = null,
    
    val confidence: Double? = null,
    
    // 健康记录 OCR 结果
    @SerialName("recordDate")
    val recordDate: String? = null,
    
    val weight: Double? = null,
    
    val height: Double? = null,
    
    @SerialName("headCircumference")
    val headCircumference: Double? = null,
    
    val hemoglobin: Double? = null,
    
    val iron: Double? = null,
    
    val calcium: Double? = null,
    
    val notes: String? = null,
    
    // 食材识别结果
    @SerialName("ingredientName")
    val ingredientName: String? = null,
    
    @SerialName("storageMethod")
    val storageMethod: String? = null,
    
    @SerialName("estimatedShelfLifeDays")
    val estimatedShelfLifeDays: Int? = null,
    
    val ingredientMinAgeMonths: Int? = null,
    
    val ingredientMaxAgeMonths: Int? = null,
    
    @SerialName("safetyTips")
    val safetyTips: String? = null
)

/**
 * 营养数据
 */
@Serializable
data class NutritionData(
    val calories: Double? = null,
    val protein: Double? = null,
    val calcium: Double? = null,
    val iron: Double? = null
)
