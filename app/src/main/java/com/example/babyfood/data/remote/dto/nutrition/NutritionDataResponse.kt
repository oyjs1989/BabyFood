package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 营养数据响应
 */
@Serializable
data class NutritionDataResponse(
    val id: Int,
    
    @SerialName("ingredientName")
    val ingredientName: String,
    
    val calories: Double,  // 每100克热量（千卡）
    val protein: Double,   // 每100克蛋白质（克）
    val calcium: Double,   // 每100克钙（毫克）
    val iron: Double,      // 每100克铁（毫克）
    
    @SerialName("vitaminA")
    val vitaminA: Double? = null,  // 每100克维生素A（μg）
    
    @SerialName("vitaminC")
    val vitaminC: Double? = null   // 每100克维生素C（毫克）
)
