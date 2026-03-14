package com.example.babyfood.data.remote.dto.nutrition

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 规则版本响应
 */
@Serializable
data class RuleVersionResponse(
    @SerialName("versionId")
    val versionId: Int,
    
    @SerialName("updatedAt")
    val updatedAt: String,
    
    @SerialName("changeLog")
    val changeLog: String? = null
)
