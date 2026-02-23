package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 质地建议响应
 */
@Serializable
data class TextureAdviceResponse(
    @SerialName("ageInMonths")
    val ageInMonths: Int,
    
    @SerialName("textureType")
    val textureType: String,  // PUREE, MASH, CHUNK, SOLID
    
    val description: String,
    
    @SerialName("developmentalStage")
    val developmentalStage: String,
    
    @SerialName("chewingAbility")
    val chewingAbility: String
)
